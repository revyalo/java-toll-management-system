package com.revyalo.toll.domain;

import java.util.Objects;

public record UserAccount(
    long id,
    String username,
    String passwordHash,
    String passwordSalt,
    UserRole role,
    String licensePlate
) {
    public UserAccount {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(passwordHash, "passwordHash");
        Objects.requireNonNull(passwordSalt, "passwordSalt");
        Objects.requireNonNull(role, "role");
    }
}
