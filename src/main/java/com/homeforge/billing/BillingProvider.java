package com.homeforge.billing;

import com.homeforge.company.domain.Company;
import com.homeforge.subscription.PlanCode;

/**
 * Proveedor de cobro de suscripciones (Stripe, Mercado Pago...). El resto del sistema solo conoce
 * planes y estados; los detalles de cada proveedor viven en su implementación.
 */
public interface BillingProvider {

    /** Identificador estable del proveedor, p. ej. "stripe". */
    String id();

    /** Si tiene credenciales configuradas en este entorno. */
    boolean isConfigured();

    /** URL de la página de pago del proveedor para contratar un plan. */
    String startCheckout(Company company, String payerEmail, PlanCode plan);

    /** URL donde el cliente administra su suscripción (tarjeta, cambio de plan, cancelación). */
    String managementUrl(Company company);
}
