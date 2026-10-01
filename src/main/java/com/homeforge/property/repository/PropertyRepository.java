package com.homeforge.property.repository;
import com.homeforge.property.domain.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByCompanyIdAndDeletedAtIsNull(UUID companyId);
    List<Property> findByPublishedTrueAndDeletedAtIsNullOrderByCreatedAtDesc();
    Optional<Property> findByIdAndPublishedTrueAndDeletedAtIsNull(UUID id);
    Optional<Property> findByIdAndCompanyIdAndDeletedAtIsNull(UUID id, UUID companyId);
}
