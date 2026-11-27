package com.movieanalyser.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

/**
 * Zaman damgaları için kullanılan saati sağlar. Saati enjekte etmek
 * (Instant.now() çağırmak yerine) testlerin sabit bir zaman kullanmasını sağlar.
 */
@ApplicationScoped
public class ClockProducer {

    @Produces
    @ApplicationScoped
    public Clock clock() {
        return Clock.systemUTC();
    }
}
