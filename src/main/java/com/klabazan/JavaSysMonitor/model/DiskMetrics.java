package com.klabazan.JavaSysMonitor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiskMetrics {
    private String name;
    private String totalSpace;
    private String freeSpace;
    private String combinedSpace;
}