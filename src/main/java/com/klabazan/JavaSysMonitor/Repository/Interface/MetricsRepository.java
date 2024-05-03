package com.klabazan.JavaSysMonitor.Repository.Interface;

import com.klabazan.JavaSysMonitor.Models.SystemMetrics;

public interface MetricsRepository {
    SystemMetrics getMetrics();
    void setMetrics(SystemMetrics metrics);  // Directly store new metrics
}
