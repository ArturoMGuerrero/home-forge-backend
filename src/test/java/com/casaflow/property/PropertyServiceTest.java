package com.casaflow.property;

import com.casaflow.property.dto.CreatePropertyRequest;
import com.casaflow.property.domain.ListingType;
import com.casaflow.property.domain.PropertyStatus;
import com.casaflow.property.repository.PropertyRepository;
import com.casaflow.property.service.PropertyService;
import com.casaflow.property.service.PropertyImageStorage;
import com.casaflow.subscription.SubscriptionValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class PropertyServiceTest {

    @Test
    void createsProperty() {
        PropertyRepository repository = Mockito.mock(PropertyRepository.class);
        PropertyImageStorage imageStorage = Mockito.mock(PropertyImageStorage.class);
        SubscriptionValidator subscriptionValidator = Mockito.mock(SubscriptionValidator.class);
        PropertyService service = new PropertyService(repository, imageStorage, subscriptionValidator);

        service.create(new CreatePropertyRequest(
                UUID.randomUUID(),
                "CF-400",
                "Casa moderna con jardín",
                "HOUSE",
                ListingType.SALE,
                PropertyStatus.AVAILABLE,
                new BigDecimal("2500000"),
                "MXN",
                "MX",
                "Querétaro",
                "Querétaro",
                "Av. Principal 100",
                new BigDecimal("20.5888"),
                new BigDecimal("-100.3899"),
                3,
                new BigDecimal("2.5"),
                new BigDecimal("220"),
                new BigDecimal("180"),
                2,
                "Casa amplia y bien ubicada.",
                "https://example.com/property.jpg",
                true,
                null, // ownerName
                null, // ownerEmail
                null, // ownerPhone
                null, // ownerPhoneSecondary
                null  // ownerNotes
        ));

        Mockito.verify(repository).save(any());
    }
}
