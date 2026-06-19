package com.klabazan.JavaSysMonitor.component;

import org.springframework.stereotype.Component;
import oshi.hardware.CentralProcessor;

@Component
public class CpuMonitor {

    private final CentralProcessor processor;
    private long[] prevTicks;

    public CpuMonitor(SystemInfo systemInfo) {
        this.processor = systemInfo.getSystemInfo()
                .getHardware()
                .getProcessor();

        this.prevTicks = processor.getSystemCpuLoadTicks();
    }

    public synchronized double getCpuLoad() {
        double load = processor.getSystemCpuLoadBetweenTicks(prevTicks);
        prevTicks = processor.getSystemCpuLoadTicks();
        return load;
    }
}
