package com.rmyndharis.idawhats.model;

/** Per-database readiness detail. Optional fields are {@code null} when absent. */
public record HealthReadyDetails(String mainDatabase, String dataDatabase) {}
