package com.homeforge.billing;

/** Error al hablar con el proveedor de pagos; el mensaje es apto para mostrarse al usuario. */
public class BillingException extends RuntimeException {

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }
}
