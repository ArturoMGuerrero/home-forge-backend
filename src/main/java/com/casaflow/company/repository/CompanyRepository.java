package com.casaflow.company.repository;
import com.casaflow.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface CompanyRepository extends JpaRepository<Company, UUID> {}
