package com.casaflow.user.service;

import com.casaflow.user.domain.Permission;
import com.casaflow.user.domain.Role;
import com.casaflow.user.domain.User;
import com.casaflow.user.domain.UserPermissionOverride;
import com.casaflow.user.repository.RoleRepository;
import com.casaflow.user.repository.UserPermissionOverrideRepository;
import com.casaflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPermissionOverrideRepository userPermissionOverrideRepository;

    public PermissionService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserPermissionOverrideRepository userPermissionOverrideRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPermissionOverrideRepository = userPermissionOverrideRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, UUID companyId, Permission permission) {
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, companyId)
                .orElse(null);
        if (user == null || !user.isActive()) {
            return false;
        }

        // ADMIN role has all permissions
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        // Check user-specific permission overrides first
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(userId);
        Optional<UserPermissionOverride> override = overrides.stream()
                .filter(o -> o.getPermissionCode() == permission)
                .findFirst();
        if (override.isPresent()) {
            return override.get().isGranted();
        }

        // Check role permissions
        if (user.getRoleId() != null) {
            Role role = roleRepository.findByIdAndCompanyIdAndDeletedAtIsNull(user.getRoleId(), companyId)
                    .orElse(null);
            if (role != null) {
                return role.getPermissions().contains(permission);
            }
        }

        // Default AGENT permissions (read-only on most modules)
        if ("AGENT".equals(user.getRole())) {
            return getDefaultAgentPermissions().contains(permission);
        }

        return false;
    }

    @Transactional(readOnly = true)
    public Set<Permission> getUserPermissions(UUID userId, UUID companyId) {
        User user = userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(userId, companyId)
                .orElse(null);
        if (user == null || !user.isActive()) {
            return Set.of();
        }

        // ADMIN has all permissions
        if ("ADMIN".equals(user.getRole())) {
            return EnumSet.allOf(Permission.class);
        }

        Set<Permission> permissions = new HashSet<>();

        // Add role permissions
        if (user.getRoleId() != null) {
            Role role = roleRepository.findByIdAndCompanyIdAndDeletedAtIsNull(user.getRoleId(), companyId)
                    .orElse(null);
            if (role != null) {
                permissions.addAll(role.getPermissions());
            }
        } else if ("AGENT".equals(user.getRole())) {
            // Default AGENT permissions
            permissions.addAll(getDefaultAgentPermissions());
        }

        // Apply user-specific overrides
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(userId);
        for (UserPermissionOverride override : overrides) {
            if (override.isGranted()) {
                permissions.add(override.getPermissionCode());
            } else {
                permissions.remove(override.getPermissionCode());
            }
        }

        return permissions;
    }

    private Set<Permission> getDefaultAgentPermissions() {
        return Set.of(
                // Leads - can view and create, but not delete
                Permission.LEAD_VIEW,
                Permission.LEAD_CREATE,
                Permission.LEAD_EDIT,

                // Properties - can view only
                Permission.PROPERTY_VIEW,

                // Documents
                Permission.DOCUMENT_VIEW,
                Permission.DOCUMENT_UPLOAD,

                // Agenda
                Permission.AGENDA_VIEW,
                Permission.AGENDA_CREATE,
                Permission.AGENDA_EDIT,

                // Reports - view only
                Permission.REPORT_VIEW
        );
    }
}
