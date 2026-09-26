package com.revyalo.toll.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TollRatePolicyTest {
    private final TollRatePolicy policy = new TollRatePolicy();

    @ParameterizedTest
    @CsvSource({
        "9.99, 9, 2.50",
        "9.99, 12, 3.00",
        "10.0, 9, 4.00",
        "19.99, 18, 5.00",
        "20.0, 11, 6.50",
        "25.0, 23, 8.00"
    })
    void calculatesRateBySizeAndTimeBand(double size, int hour, String expected) {
        BigDecimal amount = policy.calculate(
            size,
            LocalDateTime.of(2026, 1, 1, hour, 0)
        );

        assertEquals(new BigDecimal(expected), amount);
    }
}
