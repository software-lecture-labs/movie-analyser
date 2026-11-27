package com.movieanalyser.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/** JAX-RS uygulamasını /api temel yoluna kaydeder. */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
