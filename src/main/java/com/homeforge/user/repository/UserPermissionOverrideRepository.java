package com.homeforge.user.repository;

import com.homeforge.user.domain.Permission;
import com.homeforge.user.domain.UserPermissionOverride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, UserPermissionOverride.UserPermissionId> {
    List<UserPermissionOverride> findByUserId(UUID userId);

    void deleteByUserIdAndPermissionCode(UUID userId, Permission permissionCode);
}
