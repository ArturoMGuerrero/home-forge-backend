package com.homeforge.user.controller;

import com.homeforge.security.CurrentUser;
import com.homeforge.security.TokenService;
import com.homeforge.shared.validation.Validations;
import com.homeforge.user.domain.User;
import com.homeforge.user.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** "Mi cuenta": cada usuario edita sus propios datos; siempre es el usuario de la sesión. */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public record Profile(String fullName, String email, String phoneE164, String role) {
        static Profile of(User user) {
            return new Profile(user.getFullName(), user.getEmail(), user.getPhoneE164(), user.getRole());
        }
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 180) String fullName,
            @Pattern(regexp = "^$|" + Validations.PHONE_E164, message = "usa el formato internacional, por ejemplo +524421234567")
            String phoneE164
    ) {}

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank @Size(min = 8, max = 72, message = "debe tener entre 8 y 72 caracteres") String newPassword
    ) {}

    public record TokenResponse(String token) {}

    @GetMapping
    public Profile me() {
        return Profile.of(currentUser());
    }

    @PatchMapping("/profile")
    @Transactional
    public Profile updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUser();
        user.setFullName(request.fullName().trim());
        user.setPhoneE164(request.phoneE164() == null || request.phoneE164().isBlank() ? null : request.phoneE164().trim());
        return Profile.of(userRepository.save(user));
    }

    /**
     * Cambia la contraseña y cierra las demás sesiones. Devuelve un token nuevo para que la sesión
     * actual continúe sin volver a iniciar sesión.
     */
    @PostMapping("/password")
    @Transactional
    public TokenResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = currentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser distinta de la actual.");
        }
        user.changePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new TokenResponse(tokenService.issue(user));
    }

    private User currentUser() {
        CurrentUser current = CurrentUser.get().orElseThrow(() -> new AccessDeniedException("Inicia sesión para continuar"));
        return userRepository.findById(current.userId())
                .orElseThrow(() -> new AccessDeniedException("Inicia sesión para continuar"));
    }
}
