package com.movieanalyser.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.time.Clock;

/**
 * Provides the clock used for timestamps. Injecting it (instead of calling
 * Instant.now() directly) lets tests use a fixed time.
 */
@ApplicationScoped
public class ClockProducer {

    @Produces
    @ApplicationScoped
    public Clock clock() {
        return Clock.systemUTC();
    }
}
