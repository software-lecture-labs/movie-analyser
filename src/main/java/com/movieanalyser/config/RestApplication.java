package com.movieanalyser.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/** Registers the JAX-RS application under the /api base path. */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
