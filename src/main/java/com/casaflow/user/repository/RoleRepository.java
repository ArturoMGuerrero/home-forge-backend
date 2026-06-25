package com.casaflow.user.repository;

import com.casaflow.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    List<Role> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID companyId);

    Optional<Role> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);

    Optional<Role> findByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);

    boolean existsByCompanyIdAndCodeAndDeletedAtIsNull(UUID companyId, String code);
}
