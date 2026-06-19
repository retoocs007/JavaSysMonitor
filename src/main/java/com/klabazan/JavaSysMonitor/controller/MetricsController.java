package com.klabazan.JavaSysMonitor.controller;

import com.klabazan.JavaSysMonitor.model.GeneralMetric;
import com.klabazan.JavaSysMonitor.model.MetricType;
import com.klabazan.JavaSysMonitor.model.SystemMetrics;
import com.klabazan.JavaSysMonitor.service.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/metrics")
    public SystemMetrics getSystemMetrics(
            HttpServletRequest request,
            @RequestParam(value = "id", required = false, defaultValue = "noId") String id) {
        String guid = UUID.randomUUID().toString();
        log.info("GUID: {}, ID: {}, Request from IP: {}", guid, id, request.getRemoteAddr());
        return metricsService.fetchSystemMetrics(guid, id);
    }

    @GetMapping("/metrics-as-list")
    public List<GeneralMetric> getSystemMetricsAsList(
            HttpServletRequest request,
            @RequestParam(value = "id", required = false, defaultValue = "noId") String id,
            @RequestParam(required = false) List<MetricType> allowedTypes) {
        String guid = UUID.randomUUID().toString();
        log.info("GUID: {}, ID: {}, Request from IP: {}", guid, id, request.getRemoteAddr());
        return metricsService.fetchSystemMetricsAsList(guid, id, allowedTypes);
    }
}