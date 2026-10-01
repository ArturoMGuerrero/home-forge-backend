package com.homeforge.shared.exception;

import com.homeforge.auth.exception.EmailAlreadyExistsException;
import com.homeforge.billing.BillingException;
import com.homeforge.auth.exception.InvalidCredentialsException;
import com.homeforge.auth.exception.InvalidPasswordResetTokenException;
import com.homeforge.auth.exception.TooManyAttemptsException;
import com.homeforge.subscription.exception.SubscriptionExpiredException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> invalidArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    ResponseEntity<Map<String, String>> emailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<Map<String, String>> invalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    ResponseEntity<Map<String, String>> invalidPasswordResetToken(InvalidPasswordResetTokenException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage()).orElse("Validation error");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(SubscriptionExpiredException.class)
    ResponseEntity<Map<String, String>> subscriptionExpired(SubscriptionExpiredException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Map<String, String>> accessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(BillingException.class)
    ResponseEntity<Map<String, String>> billing(BillingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    ResponseEntity<Map<String, String>> tooManyAttempts(TooManyAttemptsException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, ex.getRetryAfter().toSeconds())))
                .body(Map.of("error", ex.getMessage()));
    }
}
