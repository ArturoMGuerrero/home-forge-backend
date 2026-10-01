package com.homeforge.auth;

import com.homeforge.security.TokenService;
import com.homeforge.auth.dto.AuthResponse;
import com.homeforge.auth.dto.LoginRequest;
import com.homeforge.auth.dto.RegisterRequest;
import com.homeforge.auth.exception.InvalidCredentialsException;
import com.homeforge.auth.service.AuthService;
import com.homeforge.auth.repository.PasswordResetTokenRepository;
import com.homeforge.company.domain.Company;
import com.homeforge.company.repository.CompanyRepository;
import com.homeforge.user.domain.User;
import com.homeforge.user.repository.UserRepository;
import com.homeforge.notification.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService service;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        companyRepository = mock(CompanyRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        tokenService = mock(TokenService.class);
        when(tokenService.issue(any(User.class))).thenReturn("test-token");
        service = new AuthService(
                userRepository,
                companyRepository,
                passwordEncoder,
                mock(PasswordResetTokenRepository.class),
                mock(EmailService.class),
                tokenService,
                "http://localhost:5173"
        );
    }

    @Test
    void registersCompanyAndUserWithHashedPassword() {
        UUID companyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            ReflectionTestUtils.setField(company, "id", companyId);
            return company;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", userId);
            return user;
        });

        AuthResponse response = service.register(new RegisterRequest(
                "Jorge Martínez",
                "Inmobiliaria Horizonte",
                "  JORGE@EXAMPLE.COM ",
                "+524421234567",
                "password123"
        ));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("jorge@example.com", savedUser.getEmail());
        assertNotEquals("password123", savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPasswordHash()));
        assertEquals(companyId, response.companyId());
        assertEquals(userId, response.userId());
    }

    @Test
    void logsInWithValidCredentials() {
        UUID companyId = UUID.randomUUID();
        User user = new User(
                companyId,
                "Jorge Martínez",
                "jorge@example.com",
                "+524421234567",
                passwordEncoder.encode("password123")
        );
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        Company company = new Company("Inmobiliaria Horizonte", "MX", "N/A", "MXN", "America/Mexico_City");
        ReflectionTestUtils.setField(company, "id", companyId);

        when(userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("jorge@example.com")).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        AuthResponse response = service.login(new LoginRequest("JORGE@EXAMPLE.COM", "password123"));

        assertEquals("jorge@example.com", response.email());
        assertEquals("Inmobiliaria Horizonte", response.companyName());
    }

    @Test
    void rejectsInvalidPassword() {
        User user = new User(
                UUID.randomUUID(),
                "Jorge Martínez",
                "jorge@example.com",
                "+524421234567",
                passwordEncoder.encode("password123")
        );
        when(userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("jorge@example.com")).thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> service.login(new LoginRequest("jorge@example.com", "incorrecta"))
        );
    }
}
