package com.casaflow.shared;
import com.casaflow.shared.validation.Validations;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PhoneValidationTest {
 @Test void acceptsE164(){ assertTrue("+526141234567".matches(Validations.PHONE_E164)); }
 @Test void rejectsLocalPhone(){ assertFalse("6141234567".matches(Validations.PHONE_E164)); }
}
