package com.casaflow.user.repository;

import com.casaflow.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCaseAndDeletedAtIsNull(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
    List<User> findByCompanyIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID companyId);
    long countByCompanyIdAndDeletedAtIsNull(UUID companyId);
}
