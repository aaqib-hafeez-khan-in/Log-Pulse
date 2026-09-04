package com.example.logpulse.alert;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AlertSummary {
    private final int total;
    private final int critical;
    private final int high;
    private final int medium;
    private final int low;

    private AlertSummary(int total, int critical, int high, int medium, int low) {
        this.total = total;
        this.critical = critical;
        this.high = high;
        this.medium = medium;
        this.low = low;
    }

    public static AlertSummary from(List<Alert> alerts) {
        Map<String, Long> counts = alerts.stream()
            .map(Alert::getSeverity)
            .filter(severity -> severity != null)
            .map(String::toUpperCase)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return new AlertSummary(
            alerts.size(),
            counts.getOrDefault("CRITICAL", 0L).intValue(),
            counts.getOrDefault("HIGH", 0L).intValue(),
            counts.getOrDefault("MEDIUM", 0L).intValue(),
            counts.getOrDefault("LOW", 0L).intValue()
        );
    }

    public int getTotal() {
        return total;
    }

    public int getCritical() {
        return critical;
    }

    public int getHigh() {
        return high;
    }

    public int getMedium() {
        return medium;
    }

    public int getLow() {
        return low;
    }
}
