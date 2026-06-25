package com.casaflow.user.domain;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_permissions")
@IdClass(UserPermissionOverride.UserPermissionId.class)
public class UserPermissionOverride {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "permission_code", length = 60)
    @Enumerated(EnumType.STRING)
    private Permission permissionCode;

    @Column(nullable = false)
    private boolean granted = true;

    protected UserPermissionOverride() {
    }

    public UserPermissionOverride(UUID userId, Permission permissionCode, boolean granted) {
        this.userId = userId;
        this.permissionCode = permissionCode;
        this.granted = granted;
    }

    public UUID getUserId() {
        return userId;
    }

    public Permission getPermissionCode() {
        return permissionCode;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public static class UserPermissionId implements Serializable {
        private UUID userId;
        private Permission permissionCode;

        public UserPermissionId() {
        }

        public UserPermissionId(UUID userId, Permission permissionCode) {
            this.userId = userId;
            this.permissionCode = permissionCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserPermissionId that = (UserPermissionId) o;
            return userId.equals(that.userId) && permissionCode == that.permissionCode;
        }

        @Override
        public int hashCode() {
            return userId.hashCode() + permissionCode.hashCode();
        }
    }
}
