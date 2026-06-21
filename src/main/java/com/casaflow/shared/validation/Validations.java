package com.casaflow.shared.validation;

public final class Validations {
    private Validations() {}
    public static final String PHONE_E164 = "^\\+[1-9]\\d{1,14}$";
    public static final String ISO_CURRENCY = "^[A-Z]{3}$";
    public static final String COUNTRY_CODE = "^[A-Z]{2}$";
}
