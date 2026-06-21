package com.casaflow.shared.multitenancy;

import java.util.UUID;

public final class TenantContext {
    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();
    private TenantContext() {}
    public static void setCompanyId(UUID companyId) { CURRENT.set(companyId); }
    public static UUID getCompanyId() { return CURRENT.get(); }
    public static void clear() { CURRENT.remove(); }
}
