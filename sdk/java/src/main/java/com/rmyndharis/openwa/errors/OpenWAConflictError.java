package com.rmyndharis.idawhats.errors;

/** 409 Conflict — typically an engine-not-ready condition from the backend. */
public class IdaWhatsConflictError extends IdaWhatsApiError {
    public IdaWhatsConflictError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
