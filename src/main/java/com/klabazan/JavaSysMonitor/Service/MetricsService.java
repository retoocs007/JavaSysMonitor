package com.klabazan.JavaSysMonitor.service;

import com.klabazan.JavaSysMonitor.component.CpuMonitor;
import com.klabazan.JavaSysMonitor.component.SystemInfo;
import com.klabazan.JavaSysMonitor.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.klabazan.JavaSysMonitor.model.MetricType.*;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private static final long BYTES_DIVIDER = (1024 * 1024 * 1024);
    private long[] prevTicks = null;
    private final SystemInfo systemInfo;
    private final CpuMonitor cpuMonitor;

    private static final Set<String> EXCLUDED_MOUNTS = Set.of(
            "/tmp", "/boot/efi", "/var/lib/lxcfs", "/proc", "/sys", "/dev", "/run"
    );

    private static final Set<String> EXCLUDED_FS_TYPES = Set.of(
            "proc", "sysfs", "tmpfs", "devtmpfs", "devfs", "overlay", "squashfs", "nsfs",
            "tracefs", "securityfs", "cgroup", "cgroup2", "fusectl", "debugfs", "ramfs",
            "autofs", "mqueue", "pstore", "bpf"
    );

    public SystemMetrics fetchSystemMetrics(String guid, String id) {
        log.info("GUID: {}, ID: {}, Getting System Metrics...", guid, id);

        if (prevTicks == null) {
            prevTicks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();
        }

        String cpuLoad = String.format("%.2f %%", cpuMonitor.getCpuLoad() * 100);
        String usedMemory = String.format("%.2f GB", ((double) (systemInfo.getHardware().getMemory().getTotal()
                - systemInfo.getHardware().getMemory().getAvailable())) / BYTES_DIVIDER);
        String totalMemory = systemInfo.getTotalMemory();
        String freeMemory = String.format("%.2f GB", ((double) systemInfo.getHardware().getMemory().getAvailable() / BYTES_DIVIDER));
        List<DiskMetrics> disks = getDiskMetrics(guid, id);
        String osName = systemInfo.getOsName();
        String osVersion = systemInfo.getOsVersion();
        String osArch = systemInfo.getOsArch();

        String cpuName = systemInfo.getCpuName();
        String upTime = systemInfo.getUptime();
        String mboName = systemInfo.getMboName();

        log.info("GUID: {}, ID: {}, Returning System Metrics...", guid, id);
        return new SystemMetrics(
                cpuLoad,
                usedMemory,
                totalMemory,
                freeMemory,
                disks,
                osName,
                osVersion,
                osArch,
                cpuName,
                mboName,
                upTime
        );
    }

    private List<DiskMetrics> getDiskMetrics(String guid, String id) {
        List<DiskMetrics> diskMetrics = new ArrayList<>();

        try {
            FileSystem fileSystem =
                    systemInfo.getSystemInfo()
                            .getOperatingSystem()
                            .getFileSystem();

            for (OSFileStore store : fileSystem.getFileStores()) {
                String storeMount = store.getMount();

                if (EXCLUDED_MOUNTS.contains(storeMount)) continue;
                if (EXCLUDED_FS_TYPES.contains(store.getType())) continue;
                if (storeMount.startsWith("/snap")) continue;
                if (storeMount.startsWith("/proc")) continue;
                if (storeMount.startsWith("/sys")) continue;
                if (storeMount.startsWith("/run")) continue;
                if (storeMount.startsWith("/dev")) continue;

                long totalSpace = store.getTotalSpace();
                long freeSpace = store.getUsableSpace();

                if (totalSpace <= 0) continue;

                diskMetrics.add(new DiskMetrics(
                        storeMount,
                        String.format("%d GB", totalSpace / BYTES_DIVIDER),
                        String.format("%d GB", freeSpace / BYTES_DIVIDER),
                        "Total: " + String.format("%d GB", totalSpace / BYTES_DIVIDER) +
                        "\u00A0\u00A0" +
                        "Free: " + String.format("%d GB", freeSpace / BYTES_DIVIDER)
                ));
            }
        } catch (Exception e) {
            log.error("GUID: {}, ID: {}, Failed to get disk metrics",
                    guid, id, e);
        }

        return diskMetrics;
    }

    public List<GeneralMetric> fetchSystemMetricsAsList(String guid, String id, List<MetricType> allowedTypes, List<RenameLabel> renameLabels) {
        if(allowedTypes == null || allowedTypes.isEmpty()){
            allowedTypes = Arrays.asList(MetricType.values());
        }

        SystemMetrics systemMetrics = fetchSystemMetrics(guid, id);
        List<GeneralMetric>  generalMetrics = new ArrayList<>();

        generalMetrics.add(new GeneralMetric("CPU load", systemMetrics.getCpuLoad(), CPU_LOAD));
        generalMetrics.add(new GeneralMetric("Total memory", systemMetrics.getTotalMemory(), MEM_TOTAL));
        generalMetrics.add(new GeneralMetric("Used memory", systemMetrics.getUsedMemory(), MEM_USED));
        generalMetrics.add(new GeneralMetric("Free memory", systemMetrics.getFreeMemory(), MEM_FREE));
        generalMetrics.add(new GeneralMetric("OS", systemMetrics.getOsName(), OS));
        generalMetrics.add(new GeneralMetric("Kernel", systemMetrics.getOsVersion(), KERNEL));
        generalMetrics.add(new GeneralMetric("Arch", systemMetrics.getOsArch(), ARCH));
        generalMetrics.add(new GeneralMetric("CPU", systemMetrics.getCpuName(), CPU_NAME));
        generalMetrics.add(new GeneralMetric("MBO", systemMetrics.getMboName(), MBO));
        generalMetrics.add(new GeneralMetric("Uptime", systemMetrics.getUptime(), UPTIME));

        for (DiskMetrics disk : systemMetrics.getDisks()) {
            generalMetrics.add(new GeneralMetric(disk.getName(), disk.getCombinedSpace(), DISK));
        }

        if (renameLabels != null && !renameLabels.isEmpty()) {
            Map<String, String> labelMap = renameLabels.stream()
                    .collect(Collectors.toMap(
                            RenameLabel::getOldLabel,
                            RenameLabel::getNewLabel
                    ));

            generalMetrics.forEach(metric -> {
                String newLabel = labelMap.get(metric.getLabel());
                if (newLabel != null) {
                    metric.setLabel(newLabel);
                }
            });
        }

        Map<MetricType, Integer> order = IntStream.range(0, allowedTypes.size())
                .boxed()
                .collect(Collectors.toMap(allowedTypes::get, Function.identity()));

        return generalMetrics.stream()
                .filter(metric -> order.containsKey(metric.getType()))
                .sorted(Comparator.comparingInt(metric -> order.get(metric.getType())))
                .toList();
    }
}