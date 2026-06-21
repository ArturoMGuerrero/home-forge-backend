package com.casaflow.auth.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Ya existe una cuenta con ese correo electrónico.");
    }
}
