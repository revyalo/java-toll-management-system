package com.revyalo.toll.security;

import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.UnauthorizedOperationException;
import java.util.Arrays;

public final class RoleAuthorizer {
    public void requireAny(UserPrincipal principal, UserRole... roles) {
        if (principal == null || Arrays.stream(roles).noneMatch(role -> role == principal.role())) {
            throw new UnauthorizedOperationException("El usuario no está autorizado para esta operación");
        }
    }

    public void requirePlateAccess(UserPrincipal principal, String licensePlate, UserRole... staffRoles) {
        if (principal == null) {
            throw new UnauthorizedOperationException("Se requiere una sesión autenticada");
        }
        if (principal.role() == UserRole.DRIVER) {
            if (principal.licensePlate() == null ||
                !principal.licensePlate().equalsIgnoreCase(licensePlate)) {
                throw new UnauthorizedOperationException(
                    "Un conductor solo puede consultar su matrícula asociada"
                );
            }
            return;
        }
        requireAny(principal, staffRoles);
    }
}
