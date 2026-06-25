package com.casaflow.user.repository;

import com.casaflow.user.domain.ActivityCategory;
import com.casaflow.user.domain.UserActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserActivityRepository extends JpaRepository<UserActivity, UUID> {
    Page<UserActivity> findByCompanyIdOrderByCreatedAtDesc(UUID companyId, Pageable pageable);

    Page<UserActivity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<UserActivity> findByCompanyIdAndActivityCategoryOrderByCreatedAtDesc(
            UUID companyId,
            ActivityCategory category,
            Pageable pageable
    );

    List<UserActivity> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            UUID userId,
            Instant after
    );

    long countByCompanyIdAndCreatedAtAfter(UUID companyId, Instant after);
}
