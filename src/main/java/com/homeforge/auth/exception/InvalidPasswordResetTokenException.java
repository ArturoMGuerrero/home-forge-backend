package com.homeforge.auth.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {
    public InvalidPasswordResetTokenException() {
        super("El enlace de recuperación es inválido o ha expirado.");
    }
}
