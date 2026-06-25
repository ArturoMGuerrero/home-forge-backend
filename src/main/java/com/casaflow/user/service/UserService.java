package com.casaflow.user.service;

import com.casaflow.auth.exception.EmailAlreadyExistsException;
import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.user.domain.*;
import com.casaflow.user.dto.*;
import com.casaflow.user.repository.*;
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
    private final UserSettingsRepository userSettingsRepository;
    private final UserActivityService userActivityService;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            UserSettingsRepository userSettingsRepository,
            UserActivityService userActivityService
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSettingsRepository = userSettingsRepository;
        this.userActivityService = userActivityService;
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

    @Transactional
    public UserResponse update(UUID userId, UpdateUserRequest request) {
        requireAdmin(request.companyId(), request.requesterUserId());
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }
        if (request.email() != null && !request.email().isBlank()) {
            String email = request.email().trim().toLowerCase(Locale.ROOT);
            if (!email.equals(user.getEmail()) && userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
                throw new EmailAlreadyExistsException();
            }
            user.setEmail(email);
        }
        if (request.phoneE164() != null) {
            user.setPhoneE164(clean(request.phoneE164()));
        }

        User updated = userRepository.save(user);
        userActivityService.log(
                request.companyId(),
                request.requesterUserId(),
                ActivityType.USER_UPDATED,
                ActivityCategory.USER_MANAGEMENT,
                "User",
                userId,
                "User updated: " + user.getFullName(),
                "Usuario actualizado: " + user.getFullName()
        );
        return UserResponse.from(updated);
    }

    @Transactional
    public void changeStatus(UUID userId, ChangeUserStatusRequest request) {
        requireAdmin(request.companyId(), request.requesterUserId());
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        user.setActive(request.active());
        userRepository.save(user);

        userActivityService.log(
                request.companyId(),
                request.requesterUserId(),
                request.active() ? ActivityType.USER_ACTIVATED : ActivityType.USER_DEACTIVATED,
                ActivityCategory.USER_MANAGEMENT,
                "User",
                userId,
                (request.active() ? "User activated: " : "User deactivated: ") + user.getFullName(),
                (request.active() ? "Usuario activado: " : "Usuario desactivado: ") + user.getFullName()
        );
    }

    @Transactional
    public void changeRole(UUID userId, ChangeUserRoleRequest request) {
        requireAdmin(request.companyId(), request.requesterUserId());
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        user.setRole(request.role());
        user.setRoleId(request.roleId());
        userRepository.save(user);

        userActivityService.log(
                request.companyId(),
                request.requesterUserId(),
                ActivityType.USER_ROLE_CHANGED,
                ActivityCategory.USER_MANAGEMENT,
                "User",
                userId,
                "User role changed to " + request.role() + ": " + user.getFullName(),
                "Rol de usuario cambiado a " + request.role() + ": " + user.getFullName()
        );
    }

    @Transactional(readOnly = true)
    public UserSettingsResponse getSettings(UUID userId) {
        UserSettings settings = userSettingsRepository.findById(userId)
                .orElse(new UserSettings(userId));
        return UserSettingsResponse.from(settings);
    }

    @Transactional
    public UserSettingsResponse updateSettings(UUID userId, UpdateUserSettingsRequest request) {
        UserSettings settings = userSettingsRepository.findById(userId)
                .orElse(new UserSettings(userId));

        if (request.language() != null) settings.setLanguage(request.language());
        if (request.timezone() != null) settings.setTimezone(request.timezone());
        if (request.currency() != null) settings.setCurrency(request.currency());
        if (request.emailNotifications() != null) settings.setEmailNotifications(request.emailNotifications());
        if (request.pushNotifications() != null) settings.setPushNotifications(request.pushNotifications());
        if (request.notificationNewLead() != null) settings.setNotificationNewLead(request.notificationNewLead());
        if (request.notificationLeadUpdate() != null) settings.setNotificationLeadUpdate(request.notificationLeadUpdate());
        if (request.notificationAppointment() != null) settings.setNotificationAppointment(request.notificationAppointment());
        if (request.notificationTeamActivity() != null) settings.setNotificationTeamActivity(request.notificationTeamActivity());
        if (request.theme() != null) settings.setTheme(request.theme());
        if (request.emailSignature() != null) settings.setEmailSignature(request.emailSignature());
        if (request.dashboardLayout() != null) settings.setDashboardLayout(request.dashboardLayout());

        UserSettings saved = userSettingsRepository.save(settings);
        return UserSettingsResponse.from(saved);
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
