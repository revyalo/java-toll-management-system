package com.revyalo.toll.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record TrafficFine(
    long id,
    String licensePlate,
    LocalDateTime detectedAt,
    double speedKph,
    BigDecimal amount,
    RadarType radarType,
    boolean paid
) {
    public TrafficFine {
        Objects.requireNonNull(licensePlate, "licensePlate");
        Objects.requireNonNull(detectedAt, "detectedAt");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(radarType, "radarType");
    }

    public TrafficFine withId(long generatedId) {
        return new TrafficFine(generatedId, licensePlate, detectedAt, speedKph, amount, radarType, paid);
    }
}
