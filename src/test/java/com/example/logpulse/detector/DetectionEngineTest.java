package com.example.logpulse.detector;

import com.example.logpulse.alert.Alert;
import com.example.logpulse.model.LogEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DetectionEngineTest {
    @Test
    void detectsBruteForceWithoutSuccessfulLogin() {
        List<LogEvent> events = List.of(
            event(4625, "2026-09-04T10:00:00", "alice", "10.0.0.1"),
            event(4625, "2026-09-04T10:01:00", "alice", "10.0.0.1"),
            event(4625, "2026-09-04T10:02:00", "alice", "10.0.0.1"),
            event(4625, "2026-09-04T10:03:00", "alice", "10.0.0.1"),
            event(4625, "2026-09-04T10:04:00", "alice", "10.0.0.1"),
            event(4625, "2026-09-04T10:05:00", "alice", "10.0.0.1")
        );

        List<Alert> alerts = DetectionEngine.runAll(events);

        assertEquals(1, alerts.size());
        assertEquals("HIGH", alerts.get(0).getSeverity());
        assertTrue(alerts.get(0).getMessage().contains("10.0.0.1"));
    }

    @Test
    void escalatesBruteForceWhenLoginEventuallySucceeds() {
        List<LogEvent> events = List.of(
            event(4625, "2026-09-04T10:00:00", "alice", "10.0.0.2"),
            event(4625, "2026-09-04T10:01:00", "alice", "10.0.0.2"),
            event(4625, "2026-09-04T10:02:00", "alice", "10.0.0.2"),
            event(4625, "2026-09-04T10:03:00", "alice", "10.0.0.2"),
            event(4625, "2026-09-04T10:04:00", "alice", "10.0.0.2"),
            event(4625, "2026-09-04T10:05:00", "alice", "10.0.0.2"),
            event(4624, "2026-09-04T10:06:00", "alice", "10.0.0.2")
        );

        List<Alert> alerts = DetectionEngine.runAll(events);

        assertEquals(2, alerts.size());
        assertEquals("CRITICAL", alerts.get(0).getSeverity());
        assertTrue(alerts.get(0).getMessage().contains("Brute force SUCCESS"));
        assertEquals("MEDIUM", alerts.get(1).getSeverity());
    }

    @Test
    void ignoresSuccessfulLoginOutsideSuspiciousHours() {
        List<LogEvent> events = List.of(
            event(4624, "2026-09-04T12:30:00", "alice", "10.0.0.3")
        );

        List<Alert> alerts = DetectionEngine.runAll(events);

        assertTrue(alerts.isEmpty());
    }

    @Test
    void detectsSuspiciousLoginDuringEarlyMorning() {
        List<LogEvent> events = List.of(
            event(4624, "2026-09-04T03:15:00", "alice", "10.0.0.4")
        );

        List<Alert> alerts = DetectionEngine.runAll(events);

        assertEquals(1, alerts.size());
        assertEquals("MEDIUM", alerts.get(0).getSeverity());
        assertEquals("[MEDIUM] Odd hour login: alice", alerts.get(0).toString());
    }

    private static LogEvent event(int id, String timestamp, String username, String ip) {
        return new LogEvent(id, timestamp, username, ip);
    }
}
