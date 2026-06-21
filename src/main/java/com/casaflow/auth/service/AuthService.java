package com.casaflow.auth.service;

import com.casaflow.auth.dto.AuthResponse;
import com.casaflow.auth.dto.LoginRequest;
import com.casaflow.auth.dto.RegisterRequest;
import com.casaflow.auth.exception.EmailAlreadyExistsException;
import com.casaflow.auth.exception.InvalidCredentialsException;
import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.user.domain.User;
import com.casaflow.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
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
    public void resetPassword(String email, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + normalizedEmail));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private static AuthResponse response(User user, Company company) {
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
                company.getTrialEndsAt()
        );
    }
}
