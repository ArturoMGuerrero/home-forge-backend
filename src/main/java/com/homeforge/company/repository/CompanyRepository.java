package com.homeforge.company.repository;
import com.homeforge.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface CompanyRepository extends JpaRepository<Company, UUID> {}
