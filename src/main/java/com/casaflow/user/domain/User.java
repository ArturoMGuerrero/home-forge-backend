package com.casaflow.user.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 180)
    private String fullName;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(name = "phone_e164", length = 20)
    private String phoneE164;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 40)
    private String role = "ADMIN";

    @Column(name = "role_id")
    private UUID roleId;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected User() {
    }

    public User(UUID companyId, String fullName, String email, String phoneE164, String passwordHash) {
        this(companyId, fullName, email, phoneE164, passwordHash, "ADMIN");
    }

    public User(UUID companyId, String fullName, String email, String phoneE164, String passwordHash, String role) {
        this.companyId = companyId;
        this.fullName = fullName;
        this.email = email;
        this.phoneE164 = phoneE164;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneE164() {
        return phoneE164;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneE164(String phoneE164) {
        this.phoneE164 = phoneE164;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
