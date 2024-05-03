package com.klabazan.JavaSysMonitor.Controllers;

import com.klabazan.JavaSysMonitor.Models.SystemMetrics;
import com.klabazan.JavaSysMonitor.Service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/metrics")
    public SystemMetrics getSystemMetrics() {
        return metricsService.fetchAndUpdateMetrics();
    }
}