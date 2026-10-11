package com.homeforge.lead.service;

import com.homeforge.lead.domain.Lead;
import com.homeforge.lead.domain.LeadActivity;
import com.homeforge.lead.domain.LeadActivityType;
import com.homeforge.lead.dto.PublicInquiryRequest;
import com.homeforge.lead.repository.LeadActivityRepository;
import com.homeforge.lead.repository.LeadRepository;
import com.homeforge.property.domain.ListingType;
import com.homeforge.property.domain.Property;
import com.homeforge.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Convierte en prospecto la solicitud que deja un visitante en la página pública de una propiedad.
 * <ul>
 *   <li>El prospecto se guarda en la empresa dueña de la propiedad, con origen "Sitio web", y se asigna
 *       con las reglas de asignación automática; su tarea de "Primer contacto" avisa al asesor.</li>
 *   <li>Si ya existía (mismo teléfono o correo en esa empresa) no se duplica: se agrega la solicitud a su
 *       historial y se crea una tarea para responderle.</li>
 *   <li>La solicitud queda en el historial del prospecto ligada a la propiedad, con el mensaje y el horario
 *       que prefiere para visitarla.</li>
 * </ul>
 */
@Service
public class PublicInquiryService {

    private static final Logger log = LoggerFactory.getLogger(PublicInquiryService.class);

    private final PropertyRepository propertyRepository;
    private final LeadRepository leadRepository;
    private final LeadActivityRepository activityRepository;
    private final LeadService leadService;
    private final FollowUpAutomationService followUpAutomationService;

    public PublicInquiryService(
            PropertyRepository propertyRepository,
            LeadRepository leadRepository,
            LeadActivityRepository activityRepository,
            LeadService leadService,
            FollowUpAutomationService followUpAutomationService
    ) {
        this.propertyRepository = propertyRepository;
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
        this.leadService = leadService;
        this.followUpAutomationService = followUpAutomationService;
    }

    @Transactional
    public void submit(UUID propertyId, PublicInquiryRequest request) {
        Property property = propertyRepository.findByIdAndPublishedTrueAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esta propiedad ya no está publicada."));
        if (request.isSpam()) {
            // Al robot se le responde igual que a una persona, para que no aprenda a evitar la trampa.
            log.info("Solicitud descartada como spam para la propiedad {}", propertyId);
            return;
        }

        UUID companyId = property.getCompanyId();
        String phone = request.phoneE164().trim();
        String email = blankToNull(request.email());

        Optional<Lead> existing = leadRepository
                .findFirstByCompanyIdAndPhoneE164AndDeletedAtIsNullOrderByCreatedAtDesc(companyId, phone)
                .or(() -> email == null ? Optional.empty()
                        : leadRepository.findFirstByCompanyIdAndEmailIgnoreCaseAndDeletedAtIsNullOrderByCreatedAtDesc(companyId, email));

        Lead lead;
        if (existing.isPresent()) {
            lead = existing.get();
            followUpAutomationService.createWebsiteInquiryTask(lead, property.getTitle());
        } else {
            String[] name = splitName(request.fullName());
            lead = leadService.createFromWebsite(new Lead(
                    companyId, name[0], name[1], email, phone,
                    property.getListingType() == ListingType.RENT ? "RENT" : "SALE",
                    property.getPrice(), property.getCurrencyCode(), property.getCity()
            ));
        }

        LeadActivity activity = new LeadActivity(companyId, lead.getId(), LeadActivityType.NOTE,
                activityNotes(property, request), Instant.now(), null);
        activity.setPropertyId(property.getId());
        activityRepository.save(activity);
    }

    /** "Ana María López" → ["Ana", "María López"]. Un solo nombre deja los apellidos vacíos. */
    static String[] splitName(String fullName) {
        String clean = fullName.trim().replaceAll("\\s+", " ");
        int space = clean.indexOf(' ');
        return space < 0 ? new String[]{clean, ""} : new String[]{clean.substring(0, space), clean.substring(space + 1)};
    }

    private static String activityNotes(Property property, PublicInquiryRequest request) {
        List<String> lines = new ArrayList<>();
        lines.add("Solicitud desde el sitio web por «" + property.getTitle() + "»"
                + (property.getCode() != null ? " (" + property.getCode() + ")" : "") + ".");
        lines.add("Nombre: " + request.fullName().trim());
        if (blankToNull(request.preferredVisit()) != null) {
            lines.add("Horario preferido para visitar: " + request.preferredVisit().trim());
        }
        if (blankToNull(request.message()) != null) {
            lines.add("Mensaje: " + request.message().trim());
        }
        return String.join("\n", lines);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
