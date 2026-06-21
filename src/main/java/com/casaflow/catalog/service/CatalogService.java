package com.casaflow.catalog.service;

import com.casaflow.catalog.dto.CatalogItem;
import com.casaflow.lead.domain.LeadStatus;
import com.casaflow.property.domain.PropertyStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CatalogService {

    private final Map<String, List<CatalogItem>> catalogs;

    public CatalogService() {
        catalogs = new LinkedHashMap<>();
        catalogs.put("lead-statuses", fromLeadStatuses());
        catalogs.put("lead-sources", items(
                item("WEBSITE", "Sitio web", "Website"),
                item("REFERRAL", "Referido", "Referral"),
                item("SOCIAL_MEDIA", "Redes sociales", "Social media"),
                item("PROPERTY_PORTAL", "Portal inmobiliario", "Property portal"),
                item("WALK_IN", "Visita directa", "Walk-in"),
                item("OTHER", "Otro", "Other")
        ));
        catalogs.put("property-statuses", fromPropertyStatuses());
        catalogs.put("listing-types", items(
                item("SALE", "Venta", "Sale"),
                item("RENT", "Renta", "Rent")
        ));
        catalogs.put("property-types", items(
                item("HOUSE", "Casa", "House"),
                item("APARTMENT", "Departamento", "Apartment"),
                item("LAND", "Terreno", "Land"),
                item("COMMERCIAL", "Local comercial", "Commercial"),
                item("OFFICE", "Oficina", "Office"),
                item("WAREHOUSE", "Bodega", "Warehouse")
        ));
        catalogs.put("task-statuses", items(
                item("OPEN", "Abierta", "Open"),
                item("IN_PROGRESS", "En progreso", "In progress"),
                item("COMPLETED", "Completada", "Completed"),
                item("CANCELLED", "Cancelada", "Cancelled")
        ));
        catalogs.put("document-statuses", items(
                item("PENDING", "Pendiente", "Pending"),
                item("RECEIVED", "Recibido", "Received"),
                item("APPROVED", "Aprobado", "Approved"),
                item("REJECTED", "Rechazado", "Rejected"),
                item("EXPIRED", "Vencido", "Expired")
        ));
        catalogs.put("document-types", items(
                item("IDENTIFICATION", "Identificación", "Identification"),
                item("PROOF_OF_ADDRESS", "Comprobante de domicilio", "Proof of address"),
                item("PROOF_OF_INCOME", "Comprobante de ingresos", "Proof of income"),
                item("CREDIT_REPORT", "Reporte de crédito", "Credit report"),
                item("PURCHASE_AGREEMENT", "Contrato de compraventa", "Purchase agreement"),
                item("OTHER", "Otro", "Other")
        ));
        catalogs.put("mortgage-types", items(
                item("BANK", "Crédito bancario", "Bank mortgage"),
                item("INFONAVIT", "Infonavit", "Infonavit"),
                item("FOVISSSTE", "Fovissste", "Fovissste"),
                item("COFINAVIT", "Cofinavit", "Cofinavit"),
                item("CASH", "Contado", "Cash"),
                item("OTHER", "Otro", "Other")
        ));
        catalogs.put("mortgage-statuses", items(
                item("NOT_STARTED", "Sin iniciar", "Not started"),
                item("PREQUALIFICATION", "Precalificación", "Prequalification"),
                item("DOCUMENTATION", "Documentación", "Documentation"),
                item("UNDER_REVIEW", "En revisión", "Under review"),
                item("APPROVED", "Aprobada", "Approved"),
                item("REJECTED", "Rechazada", "Rejected"),
                item("CLOSED", "Formalizada", "Closed")
        ));
        catalogs.put("countries", items(
                item("MX", "México", "Mexico"),
                item("US", "Estados Unidos", "United States")
        ));
        catalogs.put("currencies", items(
                item("MXN", "Peso mexicano", "Mexican peso"),
                item("USD", "Dólar estadounidense", "US dollar")
        ));
    }

    public List<String> names() {
        return List.copyOf(catalogs.keySet());
    }

    public Optional<List<CatalogItem>> find(String name) {
        return Optional.ofNullable(catalogs.get(name));
    }

    private static List<CatalogItem> fromLeadStatuses() {
        Map<LeadStatus, CatalogItem> labels = Map.of(
                LeadStatus.NEW, item("NEW", "Nuevo", "New"),
                LeadStatus.CONTACTED, item("CONTACTED", "Contactado", "Contacted"),
                LeadStatus.QUALIFIED, item("QUALIFIED", "Calificado", "Qualified"),
                LeadStatus.TOUR_SCHEDULED, item("TOUR_SCHEDULED", "Visita agendada", "Tour scheduled"),
                LeadStatus.TOUR_COMPLETED, item("TOUR_COMPLETED", "Visita realizada", "Tour completed"),
                LeadStatus.OFFER_MADE, item("OFFER_MADE", "Oferta realizada", "Offer made"),
                LeadStatus.UNDER_CONTRACT, item("UNDER_CONTRACT", "Bajo contrato", "Under contract"),
                LeadStatus.CLOSED, item("CLOSED", "Cerrado", "Closed"),
                LeadStatus.LOST, item("LOST", "Perdido", "Lost")
        );
        return java.util.Arrays.stream(LeadStatus.values()).map(labels::get).toList();
    }

    private static List<CatalogItem> fromPropertyStatuses() {
        Map<PropertyStatus, CatalogItem> labels = Map.of(
                PropertyStatus.AVAILABLE, item("AVAILABLE", "Disponible", "Available"),
                PropertyStatus.RESERVED, item("RESERVED", "Reservada", "Reserved"),
                PropertyStatus.UNDER_CONTRACT, item("UNDER_CONTRACT", "Bajo contrato", "Under contract"),
                PropertyStatus.SOLD, item("SOLD", "Vendida", "Sold"),
                PropertyStatus.RENTED, item("RENTED", "Rentada", "Rented"),
                PropertyStatus.INACTIVE, item("INACTIVE", "No disponible", "Unavailable")
        );
        return java.util.Arrays.stream(PropertyStatus.values()).map(labels::get).toList();
    }

    private static CatalogItem item(String code, String labelEs, String labelEn) {
        return new CatalogItem(code, labelEs, labelEn);
    }

    @SafeVarargs
    private static List<CatalogItem> items(CatalogItem... items) {
        return List.of(items);
    }
}
