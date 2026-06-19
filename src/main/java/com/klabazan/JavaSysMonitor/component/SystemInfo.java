package com.klabazan.JavaSysMonitor.component;


import lombok.Getter;
import org.springframework.stereotype.Component;
import oshi.hardware.Baseboard;
import oshi.hardware.HardwareAbstractionLayer;

import java.time.Duration;
import java.time.Instant;

@Getter
@Component
public class SystemInfo {

    private final String totalMemory;
    private final String osName;
    private final String osVersion;
    private final String osArch;
    private final String cpuName;
    private final String mboName;

    private final Instant serviceStartTime;
    private final long systemUptimeAtStartup;

    @Getter
    private final oshi.SystemInfo systemInfo;

    @Getter
    private final HardwareAbstractionLayer hardware;

    public SystemInfo() {
        this.systemInfo = new oshi.SystemInfo();
        this.hardware = systemInfo.getHardware();

        long bytesDivider = 1024L * 1024L * 1024L;

        this.totalMemory = String.format("%.2f GB",
                (double) hardware.getMemory().getTotal() / bytesDivider);

        this.osName = systemInfo.getOperatingSystem()
                .toString()
                .replace("GNU/Linux ", "");

        this.osVersion = System.getProperty("os.version");
        this.osArch = System.getProperty("os.arch");
        this.cpuName = hardware
                .getProcessor()
                .getProcessorIdentifier()
                .getName();

        Baseboard baseboard = hardware
                        .getComputerSystem()
                        .getBaseboard();

        if (!baseboard.getManufacturer().isBlank()
                && !"unknown".equalsIgnoreCase(baseboard.getManufacturer())) {

            this.mboName = baseboard.getManufacturer()
                    + " - "
                    + baseboard.getModel();
        } else {
            this.mboName = hardware
                    .getComputerSystem()
                    .getManufacturer()
                    + " - "
                    + systemInfo.getHardware()
                    .getComputerSystem()
                    .getModel()
                    .replace(" (version: Default string)","");
        }

        this.serviceStartTime = Instant.now();
        this.systemUptimeAtStartup = systemInfo.getOperatingSystem().getSystemUptime();
    }

    public String getUptime() {
        long currentUptimeSeconds =
                systemUptimeAtStartup +
                Duration.between(serviceStartTime, Instant.now()).getSeconds();

        return formatUptime(currentUptimeSeconds * 1000);
    }

    private String formatUptime(long uptimeMillis) {
        Duration duration = Duration.ofMillis(uptimeMillis);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%02dd:%02dh:%02dm:%02ds",
                days, hours, minutes, seconds);
    }
}
