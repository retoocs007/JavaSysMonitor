package com.klabazan.JavaSysMonitor.Service;

import com.klabazan.JavaSysMonitor.Models.DiskMetrics;
import com.klabazan.JavaSysMonitor.Models.SystemMetrics;
import com.klabazan.JavaSysMonitor.Repository.Interface.MetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private final long bytesDevider = (1024 * 1024 * 1024);
    private final SystemInfo systemInfo = new SystemInfo();
    private long[] prevTicks = null;

    @Autowired
    private MetricsRepository metricsRepository;

    public SystemMetrics fetchAndUpdateMetrics(String guid, String id) {
        SystemMetrics metrics = fetchSystemMetrics(guid, id);  // Method to fetch system metrics
        metricsRepository.setMetrics(metrics);
        return metrics;
    }

    private SystemMetrics fetchSystemMetrics(String guid, String id) {
        log.info("GUID: " + guid + ", ID: " + id + ", Getting System Metrics...");

        if (prevTicks == null) {
            prevTicks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();
        }
        String cpuLoad = String.format("%.2f %%", systemInfo.getHardware().getProcessor().getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        prevTicks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();

        String usedMemory = String.format("%.2f GB", ((double) (systemInfo.getHardware().getMemory().getTotal()
                - systemInfo.getHardware().getMemory().getAvailable())) / bytesDevider);
        String totalMemory = String.format("%.2f GB", ((double) systemInfo.getHardware().getMemory().getTotal() / bytesDevider));
        String freeMemory = String.format("%.2f GB", ((double) systemInfo.getHardware().getMemory().getAvailable() / bytesDevider));
        List<DiskMetrics> disks = getDiskMetrics(guid, id);
        String osName = systemInfo.getOperatingSystem().toString().replace("GNU/Linux ","");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        String cpuName = systemInfo.getHardware().getProcessor().getProcessorIdentifier().getName();
        String upTime = formatUptime(systemInfo.getOperatingSystem().getSystemUptime() * 1000);
        String mboName = systemInfo.getHardware().getComputerSystem().getManufacturer() + " - " +
                systemInfo.getHardware().getComputerSystem().getModel().replace(" (version: Default string)","");

        log.info("GUID: " + guid + ", ID: " + id + ", Returning System Metrics...");
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
        File[] roots = File.listRoots();
        for (File root : roots) {
            diskMetrics.add(new DiskMetrics(
                    root.getAbsolutePath(),
                    String.format("%d GB", (root.getTotalSpace() / bytesDevider)),
                    String.format("%d GB", (root.getFreeSpace() / bytesDevider))
            ));
        }
        Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("df -h --output=target,size,avail"); // Using df command
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            reader.readLine(); // skip the header line

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length >= 3 && tokens[0].startsWith("/mnt")) {
                    String mountPoint = tokens[0];
                    String totalSpace = tokens[1].replace("G", " GB");
                    String freeSpace = tokens[2].replace("G", " GB");
                    diskMetrics.add(new DiskMetrics(mountPoint, totalSpace, freeSpace));
                }
            }
        } catch (Exception e) {
            log.info("GUID: " + guid + ", ID: " + id + ", Error running df command, probably not Linux...");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("GUID: " + guid + ", ID: " + id + ", Failed to close the BufferedReader...", e);
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return diskMetrics;
    }

    private String formatUptime(long uptime) {
        Duration duration = Duration.ofMillis(uptime);
        long days = duration.toDays();
        long hours = duration.toHours() - days * 24;
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%02dd:%02dh:%02dm:%02ds", days, hours, minutes, seconds);
    }
}