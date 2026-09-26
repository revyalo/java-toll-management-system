package com.revyalo.toll.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SpeedFinePolicyTest {
    private final SpeedFinePolicy policy = new SpeedFinePolicy();
    private final LocalDateTime detectedAt = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Test
    void mobileRadarDoesNotFineAtTheLimit() {
        assertTrue(policy.mobileFine("1234abc", 120.0, detectedAt).isEmpty());
    }

    @Test
    void mobileRadarCreatesFineAboveLimit() {
        TrafficFine fine = policy.mobileFine("1234abc", 141.0, detectedAt).orElseThrow();

        assertEquals("1234ABC", fine.licensePlate());
        assertEquals(new BigDecimal("230.00"), fine.amount());
        assertEquals(RadarType.MOBILE, fine.radarType());
    }

    @Test
    void sectionRadarUsesAverageSpeed() {
        ActivePassage passage = new ActivePassage("1234ABC", detectedAt.minusMinutes(30));

        TrafficFine fine = policy.sectionFine(passage, detectedAt).orElseThrow();

        assertEquals(200.0, fine.speedKph(), 0.001);
        assertEquals(new BigDecimal("1500.00"), fine.amount());
        assertEquals(RadarType.SECTION, fine.radarType());
    }

    @Test
    void sectionRadarAllowsJourneyAtLimit() {
        ActivePassage passage = new ActivePassage("1234ABC", detectedAt.minusMinutes(50));

        assertTrue(policy.sectionFine(passage, detectedAt).isEmpty());
    }
}
