package com.casaflow.user.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 60)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "is_system_role", nullable = false)
    private boolean systemRole = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_code", length = 60)
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions = new HashSet<>();

    protected Role() {
    }

    public Role(UUID companyId, String code, String name, String description, boolean systemRole) {
        this.companyId = companyId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.systemRole = systemRole;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSystemRole() {
        return systemRole;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
