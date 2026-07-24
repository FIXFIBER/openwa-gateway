package com.rmyndharis.idawhats.errors;

/** Thrown when a request exceeds the configured timeout. */
public class IdaWhatsTimeoutError extends IdaWhatsError {
    public IdaWhatsTimeoutError(long timeoutMs) {
        super("Request timed out after " + timeoutMs + "ms");
    }
}
