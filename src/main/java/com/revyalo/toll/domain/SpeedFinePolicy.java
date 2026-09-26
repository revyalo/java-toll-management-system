package com.revyalo.toll.domain;

import com.revyalo.toll.validation.InputValidator;
import com.revyalo.toll.exception.InvalidVehicleException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public final class SpeedFinePolicy {
    public static final double SPEED_LIMIT_KPH = 120.0;
    public static final double SECTION_DISTANCE_KM = 100.0;

    public Optional<TrafficFine> mobileFine(
        String licensePlate,
        double speedKph,
        LocalDateTime detectedAt
    ) {
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        double speed = InputValidator.requireSpeed(speedKph);
        if (detectedAt == null) {
            throw new InvalidVehicleException("La fecha de detección es obligatoria");
        }
        if (speed <= SPEED_LIMIT_KPH) {
            return Optional.empty();
        }
        return Optional.of(new TrafficFine(
            0,
            plate,
            detectedAt,
            speed,
            mobileAmount(speed),
            RadarType.MOBILE,
            false
        ));
    }

    public Optional<TrafficFine> sectionFine(ActivePassage passage, LocalDateTime exitedAt) {
        InputValidator.requireChronological(passage.enteredAt(), exitedAt);
        double hours = Duration.between(passage.enteredAt(), exitedAt).toMillis() / 3_600_000.0;
        double averageSpeed = SECTION_DISTANCE_KM / hours;
        if (averageSpeed <= SPEED_LIMIT_KPH) {
            return Optional.empty();
        }
        return Optional.of(new TrafficFine(
            0,
            passage.licensePlate(),
            exitedAt,
            averageSpeed,
            sectionAmount(averageSpeed),
            RadarType.SECTION,
            false
        ));
    }

    private BigDecimal mobileAmount(double speed) {
        if (speed <= 130.0) return money(100);
        if (speed <= 140.0) return money(150);
        if (speed <= 150.0) return money(230);
        return money(350);
    }

    private BigDecimal sectionAmount(double speed) {
        if (speed <= 130.0) return money(150);
        if (speed <= 140.0) return money(300);
        if (speed <= 150.0) return money(700);
        return money(1500);
    }

    private BigDecimal money(long value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.UNNECESSARY);
    }
}
