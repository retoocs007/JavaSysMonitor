package com.klabazan.JavaSysMonitor.Repository;

import com.klabazan.JavaSysMonitor.Repository.Interface.MetricsRepository;
import com.klabazan.JavaSysMonitor.Models.SystemMetrics;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class MetricsRepositoryImpl implements MetricsRepository {
    private final AtomicReference<SystemMetrics> currentMetrics = new AtomicReference<>();

    @Override
    public void setMetrics(SystemMetrics metrics) {
        currentMetrics.set(metrics);
    }

    @Override
    public SystemMetrics getMetrics() {
        return currentMetrics.get();
    }
}
