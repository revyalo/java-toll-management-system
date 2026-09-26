package com.revyalo.toll.domain;

public enum UserRole {
    OPERATOR("Operario"),
    AGENT("Agente"),
    DRIVER("Conductor");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
