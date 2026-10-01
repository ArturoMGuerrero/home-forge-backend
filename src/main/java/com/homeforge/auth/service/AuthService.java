package com.homeforge.auth.service;

import com.homeforge.auth.dto.AuthResponse;
import com.homeforge.auth.dto.LoginRequest;
import com.homeforge.auth.dto.RegisterRequest;
import com.homeforge.auth.domain.PasswordResetToken;
import com.homeforge.auth.exception.EmailAlreadyExistsException;
import com.homeforge.auth.exception.InvalidCredentialsException;
import com.homeforge.auth.exception.InvalidPasswordResetTokenException;
import com.homeforge.auth.repository.PasswordResetTokenRepository;
import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.user.domain.User;
import com.homeforge.user.repository.UserRepository;
import com.homeforge.notification.service.EmailService;
import com.homeforge.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final String frontendUrl;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository passwordResetTokenRepository,
            EmailService emailService,
            TokenService tokenService,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
        this.tokenService = tokenService;
        this.frontendUrl = frontendUrl.replaceAll("/$", "");
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new EmailAlreadyExistsException();
        }

        Company company = companyRepository.save(new Company(
                request.companyName().trim(),
                "MX",
                "N/A",
                "MXN",
                "America/Mexico_City",
                email,
                request.phoneE164().trim()
        ));

        User user = userRepository.save(new User(
                company.getId(),
                request.fullName().trim(),
                email,
                request.phoneE164().trim(),
                passwordEncoder.encode(request.password())
        ));

        return response(user, company);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive() || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        Company company = companyRepository.findById(user.getCompanyId())
                .orElseThrow(InvalidCredentialsException::new);
        return response(user, company);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(normalizeEmail(email)).ifPresent(user -> {
            boolean recentlyRequested = passwordResetTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                    .map(existing -> existing.getCreatedAt().isAfter(Instant.now().minus(1, ChronoUnit.MINUTES)))
                    .orElse(false);
            if (recentlyRequested) {
                return;
            }
            passwordResetTokenRepository.deleteByUserId(user.getId());
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            passwordResetTokenRepository.save(new PasswordResetToken(
                    user.getId(), hashToken(rawToken), Instant.now().plus(30, ChronoUnit.MINUTES)
            ));
            try {
                emailService.sendPasswordReset(
                        user.getEmail(), frontendUrl + "/restablecer-contraseña?token=" + rawToken
                );
            } catch (MailException ignored) {
                // Mantener respuesta neutral para no revelar si el correo está registrado.
            }
        });
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        Instant now = Instant.now();
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hashToken(rawToken))
                .filter(candidate -> candidate.isUsableAt(now))
                .orElseThrow(InvalidPasswordResetTokenException::new);
        User user = userRepository.findById(token.getUserId())
                .filter(User::isActive)
                .orElseThrow(InvalidPasswordResetTokenException::new);
        user.changePassword(passwordEncoder.encode(newPassword));
        token.markUsed(now);
        userRepository.save(user);
        passwordResetTokenRepository.save(token);
    }

    private static String hashToken(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private AuthResponse response(User user, Company company) {
        return new AuthResponse(
                user.getId(),
                user.getCompanyId(),
                user.getFullName(),
                company.getName(),
                user.getEmail(),
                user.getRole(),
                company.getPlanCode().name(),
                company.getPlanCode().getUserLimit(),
                company.getSubscriptionStatus(),
                company.getTrialEndsAt(),
                tokenService.issue(user)
        );
    }
}
