package com.casaflow.auth;

import com.casaflow.auth.dto.AuthResponse;
import com.casaflow.auth.dto.LoginRequest;
import com.casaflow.auth.dto.RegisterRequest;
import com.casaflow.auth.exception.InvalidCredentialsException;
import com.casaflow.auth.service.AuthService;
import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.user.domain.User;
import com.casaflow.user.repository.UserRepository;
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

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        companyRepository = mock(CompanyRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        service = new AuthService(userRepository, companyRepository, passwordEncoder);
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
