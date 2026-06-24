package com.casaflow.subscription.exception;

public class SubscriptionExpiredException extends RuntimeException {
    public SubscriptionExpiredException(String message) {
        super(message);
    }
}
