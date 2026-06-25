package com.casaflow.user.repository;

import com.casaflow.user.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID companyId);

    Optional<Team> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);

    boolean existsByCompanyIdAndNameIgnoreCaseAndDeletedAtIsNull(UUID companyId, String name);
}
