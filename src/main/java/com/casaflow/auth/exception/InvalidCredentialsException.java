package com.casaflow.auth.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Correo electrónico o contraseña incorrectos.");
    }
}
