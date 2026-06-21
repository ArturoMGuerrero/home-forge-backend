package com.casaflow.user;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.user.domain.User;
import com.casaflow.user.dto.CreateCompanyUserRequest;
import com.casaflow.user.repository.UserRepository;
import com.casaflow.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class UserServiceTest {

    @Test
    void starterCanCreateTheSecondUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        UUID companyId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        User admin = new User(companyId, "Admin", "admin@example.com", null, "hash");
        ReflectionTestUtils.setField(admin, "id", adminId);
        Company company = new Company("Empresa", "MX", "QRO", "MXN", "America/Mexico_City");

        Mockito.when(userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(adminId, companyId)).thenReturn(Optional.of(admin));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(userRepository.countByCompanyIdAndDeletedAtIsNull(companyId)).thenReturn(1L);
        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            return user;
        });

        var created = service(userRepository, companyRepository).create(new CreateCompanyUserRequest(
                companyId, adminId, "Ana Asesora", "ana@example.com", "", "AGENT", "password123"
        ));

        assertEquals("AGENT", created.role());
    }

    @Test
    void starterRejectsAThirdUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        UUID companyId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();
        User admin = new User(companyId, "Admin", "admin@example.com", null, "hash");
        Company company = new Company("Empresa", "MX", "QRO", "MXN", "America/Mexico_City");

        Mockito.when(userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(adminId, companyId)).thenReturn(Optional.of(admin));
        Mockito.when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        Mockito.when(userRepository.countByCompanyIdAndDeletedAtIsNull(companyId)).thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> service(userRepository, companyRepository).create(
                new CreateCompanyUserRequest(companyId, adminId, "Tercer usuario", "third@example.com", "", "AGENT", "password123")
        ));
    }

    @Test
    void agentCannotManageUsers() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        CompanyRepository companyRepository = Mockito.mock(CompanyRepository.class);
        UUID companyId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();
        User agent = new User(companyId, "Asesor", "agent@example.com", null, "hash", "AGENT");
        Mockito.when(userRepository.findByIdAndCompanyIdAndDeletedAtIsNull(agentId, companyId)).thenReturn(Optional.of(agent));

        assertThrows(IllegalArgumentException.class, () -> service(userRepository, companyRepository).list(companyId, agentId));
    }

    private UserService service(UserRepository users, CompanyRepository companies) {
        return new UserService(users, companies, new BCryptPasswordEncoder());
    }
}
