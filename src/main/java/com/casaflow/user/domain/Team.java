package com.casaflow.user.domain;

import com.casaflow.shared.audit.AuditableEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team extends AuditableEntity {

    @Column(nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "leader_id")
    private UUID leaderId;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    protected Team() {
    }

    public Team(UUID companyId, String name, String description, UUID leaderId) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getLeaderId() {
        return leaderId;
    }

    public boolean isActive() {
        return active;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLeaderId(UUID leaderId) {
        this.leaderId = leaderId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }
}
