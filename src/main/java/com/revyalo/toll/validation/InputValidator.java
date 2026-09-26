package com.revyalo.toll.validation;

import com.revyalo.toll.exception.InvalidVehicleException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.regex.Pattern;

public final class InputValidator {
    private static final Pattern LICENSE_PLATE = Pattern.compile("[A-Z0-9-]{3,12}");

    private InputValidator() {
    }

    public static String normalizeLicensePlate(String value) {
        if (value == null) {
            throw new InvalidVehicleException("La matrícula es obligatoria");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!LICENSE_PLATE.matcher(normalized).matches()) {
            throw new InvalidVehicleException(
                "La matrícula debe contener entre 3 y 12 letras, números o guiones"
            );
        }
        return normalized;
    }

    public static double requireVehicleSize(double size) {
        if (!Double.isFinite(size) || size <= 0.0 || size > 50.0) {
            throw new InvalidVehicleException("El tamaño del vehículo debe estar entre 0 y 50");
        }
        return size;
    }

    public static double requireSpeed(double speed) {
        if (!Double.isFinite(speed) || speed <= 0.0 || speed > 400.0) {
            throw new InvalidVehicleException("La velocidad debe estar entre 0 y 400 km/h");
        }
        return speed;
    }

    public static void requireChronological(LocalDateTime enteredAt, LocalDateTime exitedAt) {
        if (enteredAt == null || exitedAt == null || !exitedAt.isAfter(enteredAt)) {
            throw new InvalidVehicleException("La salida debe ser posterior a la entrada");
        }
    }
}
