package com.klabazan.JavaSysMonitor.Controllers;

import com.klabazan.JavaSysMonitor.Models.DiskMetrics;
import com.klabazan.JavaSysMonitor.Models.SystemMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import oshi.SystemInfo;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);
    private final long bytesDevider = (1024 * 1024 * 1024);
    private final SystemInfo systemInfo = new SystemInfo();
    private long[] prevTicks = null;

    @GetMapping("/metrics")
    public SystemMetrics getSystemMetrics() {
        log.info("Getting System Metrics...");

        if (prevTicks == null) {
            prevTicks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();
        }
        String cpuLoad = String.format("%.2f %%", systemInfo.getHardware().getProcessor().getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        prevTicks = systemInfo.getHardware().getProcessor().getSystemCpuLoadTicks();

        String usedMemory = String.format("%.2f GB", ((double) (systemInfo.getHardware().getMemory().getTotal() - systemInfo.getHardware().getMemory().getAvailable())) / bytesDevider);
        String totalMemory = String.format("%.2f GB", ((double) systemInfo.getHardware().getMemory().getTotal() / bytesDevider));
        String freeMemory = String.format("%.2f GB", ((double) systemInfo.getHardware().getMemory().getAvailable() / bytesDevider));
        List<DiskMetrics> disks = getDiskMetrics();
        String osName = systemInfo.getOperatingSystem().toString().replace("GNU/Linux ","");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        String cpuName = systemInfo.getHardware().getProcessor().getProcessorIdentifier().getName();
        String upTime = formatUptime(systemInfo.getOperatingSystem().getSystemUptime() * 1000);
        String mboName = systemInfo.getHardware().getComputerSystem().getManufacturer() + " - " +
                systemInfo.getHardware().getComputerSystem().getModel().replace(" (version: Default string)","");

        log.info("Returning System Metrics...");
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

    private List<DiskMetrics> getDiskMetrics() {
        List<DiskMetrics> diskMetrics = new ArrayList<>();
        File[] roots = File.listRoots();
        for (File root : roots) {
            diskMetrics.add(new DiskMetrics(
                    root.getAbsolutePath(),
                    String.format("%d GB", (root.getTotalSpace() / bytesDevider)),
                    String.format("%d GB", (root.getFreeSpace() / bytesDevider))
            ));
        }
        try {
            Process process = Runtime.getRuntime().exec("df -h --output=target,size,avail"); // Using df command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
            log.info("Error running df command, probably not Linux...");
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