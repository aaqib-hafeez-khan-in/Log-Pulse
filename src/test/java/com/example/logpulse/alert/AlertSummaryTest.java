package com.example.logpulse.alert;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertSummaryTest {
    @Test
    void countsAlertsBySeverity() {
        List<Alert> alerts = List.of(
            new Alert("Critical event", "CRITICAL"),
            new Alert("High event", "HIGH"),
            new Alert("Another high event", "HIGH"),
            new Alert("Medium event", "MEDIUM"),
            new Alert("Low event", "LOW")
        );

        AlertSummary summary = AlertSummary.from(alerts);

        assertEquals(5, summary.getTotal());
        assertEquals(1, summary.getCritical());
        assertEquals(2, summary.getHigh());
        assertEquals(1, summary.getMedium());
        assertEquals(1, summary.getLow());
    }

    @Test
    void treatsSeverityCaseInsensitively() {
        AlertSummary summary = AlertSummary.from(List.of(
            new Alert("Critical event", "critical"),
            new Alert("High event", "High")
        ));

        assertEquals(1, summary.getCritical());
        assertEquals(1, summary.getHigh());
    }

    @Test
    void handlesUnknownAndMissingSeverityWithoutDroppingAlerts() {
        AlertSummary summary = AlertSummary.from(List.of(
            new Alert("Unknown event", "INFO"),
            new Alert("Missing severity", null)
        ));

        assertEquals(2, summary.getTotal());
        assertEquals(0, summary.getCritical());
        assertEquals(0, summary.getHigh());
        assertEquals(0, summary.getMedium());
        assertEquals(0, summary.getLow());
    }

    @Test
    void handlesEmptyAlertList() {
        AlertSummary summary = AlertSummary.from(List.of());

        assertEquals(0, summary.getTotal());
        assertEquals(0, summary.getCritical());
        assertEquals(0, summary.getHigh());
        assertEquals(0, summary.getMedium());
        assertEquals(0, summary.getLow());
    }
}
