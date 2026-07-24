package com.rmyndharis.idawhats.resources;

import com.rmyndharis.idawhats.IdaWhatsClient;
import com.rmyndharis.idawhats.http.HttpMethod;
import com.rmyndharis.idawhats.model.HealthReadyResponse;
import com.rmyndharis.idawhats.model.HealthResponse;

/** Health resource — connectivity and readiness probes. */
public final class HealthResource {
    private final IdaWhatsClient client;

    public HealthResource(IdaWhatsClient client) {
        this.client = client;
    }

    /** General health (also returns the running version). */
    public HealthResponse check() {
        return client.request(HttpMethod.GET, "/api/health", null, null, HealthResponse.class);
    }

    /** Kubernetes liveness probe. */
    public HealthResponse live() {
        return client.request(HttpMethod.GET, "/api/health/live", null, null, HealthResponse.class);
    }

    /** Kubernetes readiness probe — checks both DB connections. */
    public HealthReadyResponse ready() {
        return client.request(HttpMethod.GET, "/api/health/ready", null, null, HealthReadyResponse.class);
    }
}
