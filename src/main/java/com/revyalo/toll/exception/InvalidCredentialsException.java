package com.revyalo.toll.exception;

public final class InvalidCredentialsException extends TollSystemException {
    public InvalidCredentialsException() {
        super("Usuario o contraseña incorrectos");
    }
}
