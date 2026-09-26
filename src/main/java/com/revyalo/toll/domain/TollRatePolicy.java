package com.revyalo.toll.domain;

import com.revyalo.toll.validation.InputValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class TollRatePolicy {
    public BigDecimal calculate(double vehicleSize, LocalDateTime exitedAt) {
        double size = InputValidator.requireVehicleSize(vehicleSize);
        Objects.requireNonNull(exitedAt, "exitedAt");
        boolean firstBand = exitedAt.getHour() <= 11;

        if (size < 10.0) {
            return money(firstBand ? "2.50" : "3.00");
        }
        if (size < 20.0) {
            return money(firstBand ? "4.00" : "5.00");
        }
        return money(firstBand ? "6.50" : "8.00");
    }

    private BigDecimal money(String value) {
        return new BigDecimal(value);
    }
}
