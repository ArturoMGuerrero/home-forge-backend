package com.homeforge.security;

import com.homeforge.user.domain.User;
import com.homeforge.user.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Convierte el token en el usuario actual consultando la base en cada petición: si el usuario
 * fue desactivado, eliminado o cambió de rol, el cambio aplica de inmediato aunque el token siga vigente.
 */
@Component
class CurrentUserConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserRepository userRepository;

    CurrentUserConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID companyId = UUID.fromString(jwt.getClaimAsString(TokenService.COMPANY_CLAIM));
        User user = userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null && u.isActive() && u.getCompanyId().equals(companyId))
                .orElseThrow(() -> new InvalidBearerTokenException("Sesión no válida"));
        return new CurrentUserAuthentication(
                new CurrentUser(user.getId(), user.getCompanyId(), user.getEmail(), user.getRole()), jwt);
    }
}
