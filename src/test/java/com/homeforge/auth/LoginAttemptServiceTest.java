package com.homeforge.auth;

import com.homeforge.auth.exception.TooManyAttemptsException;
import com.homeforge.auth.service.LoginAttemptService;
import com.homeforge.auth.service.LoginAttemptService.Action;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginAttemptServiceTest {

    /** Reloj que la prueba adelanta a voluntad. */
    static final class MovableClock extends Clock {
        Instant now = Instant.parse("2026-10-01T12:00:00Z");
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advance(Duration duration) { now = now.plus(duration); }
    }

    private static LoginAttemptService service(Clock clock) throws Exception {
        Constructor<LoginAttemptService> constructor = LoginAttemptService.class.getDeclaredConstructor(Clock.class, boolean.class);
        constructor.setAccessible(true);
        return constructor.newInstance(clock, true);
    }

    @Test
    void locksAccountAfterFiveFailuresForFifteenMinutes() throws Exception {
        MovableClock clock = new MovableClock();
        LoginAttemptService attempts = service(clock);

        for (int i = 0; i < 4; i++) {
            attempts.recordFailure("Ana@Example.com");
        }
        assertDoesNotThrow(() -> attempts.checkNotLocked("ana@example.com"));

        attempts.recordFailure("ana@example.com");
        assertThrows(TooManyAttemptsException.class, () -> attempts.checkNotLocked("ANA@example.com"));

        clock.advance(Duration.ofMinutes(14));
        assertThrows(TooManyAttemptsException.class, () -> attempts.checkNotLocked("ana@example.com"));
        clock.advance(Duration.ofMinutes(2));
        assertDoesNotThrow(() -> attempts.checkNotLocked("ana@example.com"));
    }

    @Test
    void successResetsTheFailureCount() throws Exception {
        LoginAttemptService attempts = service(new MovableClock());
        for (int i = 0; i < 4; i++) {
            attempts.recordFailure("ana@example.com");
        }
        attempts.recordSuccess("ana@example.com");
        attempts.recordFailure("ana@example.com");
        assertDoesNotThrow(() -> attempts.checkNotLocked("ana@example.com"));
    }

    @Test
    void limitsRequestsPerIpPerMinute() throws Exception {
        MovableClock clock = new MovableClock();
        LoginAttemptService attempts = service(clock);
        for (int i = 0; i < 10; i++) {
            attempts.checkRate(Action.LOGIN, "10.0.0.1");
        }
        assertThrows(TooManyAttemptsException.class, () -> attempts.checkRate(Action.LOGIN, "10.0.0.1"));
        assertDoesNotThrow(() -> attempts.checkRate(Action.LOGIN, "10.0.0.2"));

        clock.advance(Duration.ofSeconds(61));
        assertDoesNotThrow(() -> attempts.checkRate(Action.LOGIN, "10.0.0.1"));
    }
}
