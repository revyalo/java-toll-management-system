package com.revyalo.toll.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public record TollTicket(
    long id,
    String licensePlate,
    double vehicleSize,
    LocalDateTime enteredAt,
    LocalDateTime exitedAt,
    BigDecimal amount
) {
    public TollTicket {
        Objects.requireNonNull(licensePlate, "licensePlate");
        Objects.requireNonNull(enteredAt, "enteredAt");
        Objects.requireNonNull(exitedAt, "exitedAt");
        Objects.requireNonNull(amount, "amount");
    }

    public TollTicket withId(long generatedId) {
        return new TollTicket(generatedId, licensePlate, vehicleSize, enteredAt, exitedAt, amount);
    }
}
