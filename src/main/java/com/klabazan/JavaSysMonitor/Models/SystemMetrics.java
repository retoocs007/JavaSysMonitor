package com.klabazan.JavaSysMonitor.Models;

import java.util.List;

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

    public SystemMetrics(String cpuLoad, String usedMemory, String totalMemory, String freeMemory,
                         List<DiskMetrics> disks, String osName, String osVersion, String osArch,
                         String cpuName, String mboName, String uptime) {
        this.cpuLoad = cpuLoad;
        this.usedMemory = usedMemory;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
        this.disks = disks;
        this.osName = osName;
        this.osVersion = osVersion;
        this.osArch = osArch;
        this.cpuName = cpuName;
        this.mboName = mboName;
        this.uptime = uptime;
    }

    public String getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(String cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(String totalMemory) {
        this.totalMemory = totalMemory;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

    public List<DiskMetrics> getDisks() {
        return disks;
    }

    public void setDisks(List<DiskMetrics> disks) {
        this.disks = disks;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getCpuName() {
        return cpuName;
    }

    public void setCpuName(String cpuName) {
        this.cpuName = cpuName;
    }

    public String getMboName() {
        return mboName;
    }

    public void setMboName(String mboName) {
        this.mboName = mboName;
    }
}