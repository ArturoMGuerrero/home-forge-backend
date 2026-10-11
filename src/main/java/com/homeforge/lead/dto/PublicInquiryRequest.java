package com.homeforge.lead.dto;

import com.homeforge.shared.validation.Validations;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Solicitud de información que deja un visitante en la página pública de una propiedad.
 *
 * @param fullName        nombre completo del visitante
 * @param phoneE164       teléfono con lada internacional (obligatorio: es como lo contacta el asesor)
 * @param email           correo opcional
 * @param message         mensaje libre opcional
 * @param preferredVisit  cuándo le gustaría visitar, en texto libre (p. ej. "sábado por la mañana")
 * @param acceptsPrivacy  aceptó el aviso de privacidad
 * @param website         trampa para robots: el formulario lo oculta, así que una persona lo deja vacío
 */
public record PublicInquiryRequest(
        @NotBlank @Size(min = 2, max = 160) String fullName,
        @NotBlank @Pattern(regexp = Validations.PHONE_E164, message = "Usa el formato internacional, por ejemplo +524421234567") String phoneE164,
        @Email @Size(max = 180) String email,
        @Size(max = 1000) String message,
        @Size(max = 120) String preferredVisit,
        @AssertTrue(message = "Debes aceptar el aviso de privacidad") boolean acceptsPrivacy,
        String website
) {

    /** Un robot llenó el campo oculto. */
    public boolean isSpam() {
        return website != null && !website.isBlank();
    }
}
