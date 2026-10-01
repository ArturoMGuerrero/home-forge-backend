package com.homeforge.auth.service;

import com.homeforge.auth.exception.TooManyAttemptsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Frena ataques de fuerza bruta en el acceso:
 * <ul>
 *   <li>por cuenta: 5 contraseñas incorrectas seguidas bloquean el correo 15 minutos;</li>
 *   <li>por IP: un máximo de peticiones por minuto a login, registro y recuperación.</li>
 * </ul>
 * Vive en memoria: basta con una instancia del backend. Con varias, mover a Redis o similar.
 */
@Service
public class LoginAttemptService {

    static final int MAX_FAILURES = 5;
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);
    static final Duration WINDOW = Duration.ofMinutes(1);
    private static final int MAX_TRACKED_KEYS = 50_000;

    public enum Action {
        LOGIN(10), REGISTER(5), PASSWORD_RESET(5);

        final int perMinute;

        Action(int perMinute) {
            this.perMinute = perMinute;
        }
    }

    private record Failures(int count, Instant lockedUntil) {}

    private final Clock clock;
    private final boolean enabled;
    private final Map<String, Failures> failuresByEmail = new ConcurrentHashMap<>();
    private final Map<String, Deque<Instant>> requestsByIp = new ConcurrentHashMap<>();

    @Autowired
    public LoginAttemptService(@Value("${app.auth.rate-limit.enabled:true}") boolean enabled) {
        this(Clock.systemUTC(), enabled);
    }

    LoginAttemptService(Clock clock, boolean enabled) {
        this.clock = clock;
        this.enabled = enabled;
    }

    /** Lanza {@link TooManyAttemptsException} si la IP superó el límite por minuto para esta acción. */
    public void checkRate(Action action, String ip) {
        if (!enabled) return;
        Instant now = clock.instant();
        Deque<Instant> window = requestsByIp.computeIfAbsent(action + ":" + ip, key -> new ArrayDeque<>());
        synchronized (window) {
            while (!window.isEmpty() && window.peekFirst().isBefore(now.minus(WINDOW))) {
                window.pollFirst();
            }
            if (window.size() >= action.perMinute) {
                Duration retry = Duration.between(now, window.peekFirst().plus(WINDOW));
                throw new TooManyAttemptsException("Demasiados intentos. Espera un momento e intenta de nuevo.", retry);
            }
            window.addLast(now);
        }
        trimIfHuge(requestsByIp);
    }

    /** Lanza {@link TooManyAttemptsException} si la cuenta está bloqueada por contraseñas incorrectas. */
    public void checkNotLocked(String email) {
        if (!enabled) return;
        Failures failures = failuresByEmail.get(key(email));
        Instant now = clock.instant();
        if (failures != null && failures.lockedUntil() != null && failures.lockedUntil().isAfter(now)) {
            Duration retry = Duration.between(now, failures.lockedUntil());
            long minutes = Math.max(1, (retry.toSeconds() + 59) / 60);
            throw new TooManyAttemptsException(
                    "Demasiados intentos fallidos. Por seguridad, espera " + minutes + " min o restablece tu contraseña.", retry);
        }
    }

    public void recordFailure(String email) {
        if (!enabled) return;
        Instant now = clock.instant();
        failuresByEmail.compute(key(email), (k, current) -> {
            int count = (current == null || (current.lockedUntil() != null && !current.lockedUntil().isAfter(now)))
                    ? 1 : current.count() + 1;
            return new Failures(count, count >= MAX_FAILURES ? now.plus(LOCK_DURATION) : null);
        });
        trimIfHuge(failuresByEmail);
    }

    public void recordSuccess(String email) {
        failuresByEmail.remove(key(email));
    }

    private static String key(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    /** Evita que un atacante llene la memoria con correos o IPs inventados. */
    private static void trimIfHuge(Map<String, ?> map) {
        if (map.size() > MAX_TRACKED_KEYS) {
            map.clear();
        }
    }
}
