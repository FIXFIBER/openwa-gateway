package com.rmyndharis.idawhats.errors;

/** 401 Unauthorized — missing or invalid API key. */
public class IdaWhatsAuthError extends IdaWhatsApiError {
    public IdaWhatsAuthError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
