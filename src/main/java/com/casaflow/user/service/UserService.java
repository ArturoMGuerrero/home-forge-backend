package com.casaflow.user.service;

import com.casaflow.auth.exception.EmailAlreadyExistsException;
import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.user.domain.User;
import com.casaflow.user.dto.CreateCompanyUserRequest;
import com.casaflow.user.dto.UserListResponse;
import com.casaflow.user.dto.UserResponse;
import com.casaflow.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserListResponse list(UUID companyId, UUID requesterUserId) {
        requireAdmin(companyId, requesterUserId);
        Company company = findCompany(companyId);
        var users = userRepository.findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtAsc(companyId);
        return new UserListResponse(
                company.getPlanCode().name(),
                company.getPlanCode().getUserLimit(),
                users.size(),
                users.stream().map(UserResponse::from).toList()
        );
    }

    @Transactional
    public UserResponse create(CreateCompanyUserRequest request) {
        requireAdmin(request.companyId(), request.requesterUserId());
        Company company = findCompany(request.companyId());
        long usedSeats = userRepository.countByCompanyIdAndDeletedAtIsNull(request.companyId());
        if (usedSeats >= company.getPlanCode().getUserLimit()) {
            throw new IllegalArgumentException(
                    "El plan " + company.getPlanCode().name() + " permite máximo "
                            + company.getPlanCode().getUserLimit() + " usuarios."
            );
        }
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
            throw new EmailAlreadyExistsException();
        }
        User user = new User(
                request.companyId(),
                request.fullName().trim(),
                email,
                clean(request.phoneE164()),
                passwordEncoder.encode(request.password()),
                request.role()
        );
        return UserResponse.from(userRepository.save(user));
    }

    private void requireAdmin(UUID companyId, UUID requesterUserId) {
        User requester = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(requesterUserId, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario administrador no encontrado."));
        if (!"ADMIN".equals(requester.getRole())) {
            throw new IllegalArgumentException("Solo un administrador puede gestionar usuarios.");
        }
    }

    private Company findCompany(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada."));
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
