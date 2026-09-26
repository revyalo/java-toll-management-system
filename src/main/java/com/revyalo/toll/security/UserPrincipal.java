package com.revyalo.toll.security;

import com.revyalo.toll.domain.UserRole;
import java.util.Objects;

public record UserPrincipal(String username, UserRole role, String licensePlate) {
    public UserPrincipal {
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(role, "role");
    }
}
