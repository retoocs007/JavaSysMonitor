package com.klabazan.JavaSysMonitor.Models;

public class DiskMetrics {
    private String name;
    private String totalSpace;
    private String freeSpace;

    public DiskMetrics(String name, String totalSpace, String freeSpace) {
        this.name = name;
        this.totalSpace = totalSpace;
        this.freeSpace = freeSpace;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace;
    }

    public String getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(String freeSpace) {
        this.freeSpace = freeSpace;
    }
}