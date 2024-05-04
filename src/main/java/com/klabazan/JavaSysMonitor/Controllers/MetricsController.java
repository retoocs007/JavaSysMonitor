package com.klabazan.JavaSysMonitor.Controllers;

import com.klabazan.JavaSysMonitor.Models.SystemMetrics;
import com.klabazan.JavaSysMonitor.Service.MetricsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/metrics")
    public SystemMetrics getSystemMetrics(HttpServletRequest request,
    @RequestParam(value = "id", required = false, defaultValue = "noId") String id) {
        String guid = UUID.randomUUID().toString();
        log.info("GUID: " + guid + ", ID: " + id + ", Request from IP: " + request.getRemoteAddr());
        return metricsService.fetchAndUpdateMetrics(guid, id);
    }
}