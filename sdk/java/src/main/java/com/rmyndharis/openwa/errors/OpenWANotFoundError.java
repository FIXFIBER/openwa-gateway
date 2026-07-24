package com.rmyndharis.idawhats.errors;

/** 404 Not Found. */
public class IdaWhatsNotFoundError extends IdaWhatsApiError {
    public IdaWhatsNotFoundError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
