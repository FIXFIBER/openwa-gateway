package com.rmyndharis.idawhats.errors;

/** Base class for every error thrown by the SDK. */
public class IdaWhatsError extends RuntimeException {
    public IdaWhatsError(String message) {
        super(message);
    }
}
