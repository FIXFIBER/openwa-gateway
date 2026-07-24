package com.rmyndharis.idawhats.model;

/** Progress counters for a bulk-send batch. */
public record BatchProgress(int total, int sent, int failed, int pending, int cancelled) {}
