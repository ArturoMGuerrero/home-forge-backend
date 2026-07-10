package com.casaflow.payment.service;

import com.casaflow.company.domain.Company;
import com.casaflow.company.repository.CompanyRepository;
import com.casaflow.payment.dto.PaymentLinkResponse;
import com.casaflow.payment.dto.PaymentStatusResponse;
import com.casaflow.subscription.PlanCode;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.resources.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MercadoPagoService {
    private final CompanyRepository companyRepository;
    private final String frontendUrl;
    private final BigDecimal starterPrice;
    private final BigDecimal proPrice;
    private final BigDecimal businessPrice;

    public MercadoPagoService(
            CompanyRepository companyRepository,
            @Value("${mercadopago.access-token}") String accessToken,
            @Value("${app.frontend.url:http://localhost:5174}") String frontendUrl,
            @Value("${mercadopago.plan.starter.price:299.00}") String starterPriceStr,
            @Value("${mercadopago.plan.pro.price:999.00}") String proPriceStr,
            @Value("${mercadopago.plan.business.price:3999.00}") String businessPriceStr
    ) {
        this.companyRepository = companyRepository;
        this.frontendUrl = frontendUrl;
        // Crear BigDecimal desde String para mantener precisión y escala correcta
        this.starterPrice = new BigDecimal(starterPriceStr);
        this.proPrice = new BigDecimal(proPriceStr);
        this.businessPrice = new BigDecimal(businessPriceStr);
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    @Transactional
    public PaymentLinkResponse createSubscription(UUID companyId, String planCode, String payerEmail) {
        Company company = findCompany(companyId);

        // Todos los planes (STARTER, PRO, BUSINESS) requieren pago vía Mercado Pago
        PlanCode plan = PlanCode.valueOf(planCode.toUpperCase());
        BigDecimal price = getPriceForPlan(plan);

        try {
            // Crear preferencia de pago (pago único mensual)
            // Asegurar que el precio tenga 2 decimales para Mercado Pago
            BigDecimal priceWithDecimals = price.setScale(2, java.math.RoundingMode.HALF_UP);

            System.out.println("🔍 DEBUG - Precio original: " + price);
            System.out.println("🔍 DEBUG - Precio con decimales: " + priceWithDecimals);
            System.out.println("🔍 DEBUG - Precio toString: " + priceWithDecimals.toString());
            System.out.println("🔍 DEBUG - Precio toPlainString: " + priceWithDecimals.toPlainString());

            PreferenceItemRequest item = PreferenceItemRequest.builder()
                    .title("HomeForge - Plan " + plan.name() + " (Mensual)")
                    .description("Suscripción mensual al plan " + plan.name())
                    .quantity(1)
                    .currencyId("MXN")
                    .unitPrice(priceWithDecimals)
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(item);

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(frontendUrl + "/payment/success?plan=" + planCode)
                    .failure(frontendUrl + "/payment/failure")
                    .pending(frontendUrl + "/payment/pending")
                    .build();

            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .externalReference(companyId.toString())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(request);

            return new PaymentLinkResponse(
                    preference.getInitPoint(),
                    preference.getId(),
                    preference.getSandboxInitPoint()
            );

        } catch (MPApiException e) {
            String errorDetails = String.format(
                "Error de API de Mercado Pago - Status: %d, Message: %s, API Response: %s",
                e.getStatusCode(),
                e.getMessage(),
                e.getApiResponse() != null ? e.getApiResponse().getContent() : "No content"
            );
            System.err.println("❌ " + errorDetails);
            throw new RuntimeException(errorDetails, e);
        } catch (MPException e) {
            System.err.println("❌ Error al crear preferencia: " + e.getMessage());
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentStatus(UUID companyId) {
        Company company = findCompany(companyId);

        String computedStatus = company.getComputedSubscriptionStatus();
        boolean hasActiveSubscription = "ACTIVE".equals(computedStatus) || "TRIAL".equals(computedStatus);

        return new PaymentStatusResponse(
                company.getId(),
                company.getPlanCode().name(),
                computedStatus,
                company.getPaymentMethod(),
                company.getLastPaymentStatus(),
                company.getLastPaymentAt(),
                company.getNextBillingAt(),
                hasActiveSubscription,
                company.getMercadoPagoSubscriptionId()
        );
    }

    /**
     * Procesa pagos procesados de Mercado Pago
     * Este método es llamado desde el webhook cuando se recibe una notificación de pago
     */
    @Transactional
    public void processPayment(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.parseLong(paymentId));

            String externalReference = payment.getExternalReference();
            if (externalReference == null) {
                System.out.println("⚠️ No external reference found for payment: " + paymentId);
                return;
            }

            UUID companyId = UUID.fromString(externalReference);
            Company company = findCompany(companyId);

            String status = payment.getStatus();
            String subscriptionStatus = switch (status) {
                case "approved" -> "ACTIVE";
                case "pending", "in_process" -> "PENDING";
                case "rejected", "cancelled", "charged_back" -> "SUSPENDED";
                default -> company.getSubscriptionStatus();
            };

            // Si el pago fue aprobado, actualizar el plan y dar 30 días de acceso
            if ("approved".equals(status)) {
                company.updateSubscriptionStatus(subscriptionStatus, calculateNextBillingDate());
                company.updatePaymentInfo("mercadopago", status);
                System.out.println("✅ Pago aprobado para company " + companyId + " - Plan activo por 30 días");
            } else {
                company.updatePaymentInfo("mercadopago", status);
                System.out.println("⚠️ Pago en estado " + status + " para company " + companyId);
            }

            companyRepository.save(company);

        } catch (Exception e) {
            System.err.println("❌ Error processing payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Company findCompany(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
    }

    private BigDecimal getPriceForPlan(PlanCode plan) {
        return switch (plan) {
            case STARTER -> starterPrice;
            case PRO -> proPrice;
            case BUSINESS -> businessPrice;
        };
    }

    private java.time.Instant calculateNextBillingDate() {
        return java.time.Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS);
    }
}
