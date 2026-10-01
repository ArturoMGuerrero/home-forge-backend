package com.homeforge.auth.controller;

import com.homeforge.auth.dto.AuthResponse;
import com.homeforge.auth.dto.LoginRequest;
import com.homeforge.auth.dto.RegisterRequest;
import com.homeforge.auth.dto.ConfirmPasswordResetRequest;
import com.homeforge.auth.dto.RequestPasswordResetRequest;
import com.homeforge.auth.exception.InvalidCredentialsException;
import com.homeforge.auth.service.AuthService;
import com.homeforge.auth.service.LoginAttemptService;
import com.homeforge.auth.service.LoginAttemptService.Action;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final LoginAttemptService attempts;

    public AuthController(AuthService service, LoginAttemptService attempts) {
        this.service = service;
        this.attempts = attempts;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest http) {
        attempts.checkRate(Action.REGISTER, http.getRemoteAddr());
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        attempts.checkRate(Action.LOGIN, http.getRemoteAddr());
        attempts.checkNotLocked(request.email());
        try {
            AuthResponse response = service.login(request);
            attempts.recordSuccess(request.email());
            return response;
        } catch (InvalidCredentialsException ex) {
            attempts.recordFailure(request.email());
            throw ex;
        }
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ConfirmPasswordResetRequest request, HttpServletRequest http) {
        attempts.checkRate(Action.PASSWORD_RESET, http.getRemoteAddr());
        service.resetPassword(request.token(), request.newPassword());
    }

    @PostMapping("/request-password-reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request, HttpServletRequest http) {
        attempts.checkRate(Action.PASSWORD_RESET, http.getRemoteAddr());
        service.requestPasswordReset(request.email());
    }
}
