package com.revyalo.toll.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record AuditEvent(
    LocalDateTime occurredAt,
    String username,
    UserRole role,
    String action,
    String entityType,
    String detail
) {
    public AuditEvent {
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(entityType, "entityType");
        Objects.requireNonNull(detail, "detail");
    }
}
