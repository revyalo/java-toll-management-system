package com.revyalo.toll.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record ActivePassage(String licensePlate, LocalDateTime enteredAt) {
    public ActivePassage {
        Objects.requireNonNull(licensePlate, "licensePlate");
        Objects.requireNonNull(enteredAt, "enteredAt");
    }
}
