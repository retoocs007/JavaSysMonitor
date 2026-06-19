package com.klabazan.JavaSysMonitor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SystemMetrics {
    private String cpuLoad;
    private String usedMemory;
    private String totalMemory;
    private String freeMemory;
    private List<DiskMetrics> disks;
    private String osName;
    private String osVersion;
    private String osArch;
    private String cpuName;
    private String mboName;
    private String uptime;
}