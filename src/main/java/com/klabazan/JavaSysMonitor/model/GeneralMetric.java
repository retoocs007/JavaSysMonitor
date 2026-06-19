package com.klabazan.JavaSysMonitor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeneralMetric {
    private String label;
    private String value;
    private MetricType type;
}
