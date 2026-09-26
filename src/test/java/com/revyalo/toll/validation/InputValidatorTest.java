package com.revyalo.toll.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revyalo.toll.exception.InvalidVehicleException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class InputValidatorTest {
    @Test
    void normalizesLicensePlate() {
        assertEquals("1234ABC", InputValidator.normalizeLicensePlate(" 1234abc "));
    }

    @Test
    void rejectsUnsafeLicensePlate() {
        assertThrows(
            InvalidVehicleException.class,
            () -> InputValidator.normalizeLicensePlate("../../file")
        );
    }

    @Test
    void rejectsNonFiniteVehicleSize() {
        assertThrows(
            InvalidVehicleException.class,
            () -> InputValidator.requireVehicleSize(Double.NaN)
        );
    }

    @Test
    void exitMustBeAfterEntry() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 10, 0);
        assertThrows(
            InvalidVehicleException.class,
            () -> InputValidator.requireChronological(now, now)
        );
    }
}
