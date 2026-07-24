package com.rmyndharis.idawhats.errors;

/** 403 Forbidden — the API key's role is insufficient for this endpoint. */
public class IdaWhatsForbiddenError extends IdaWhatsApiError {
    public IdaWhatsForbiddenError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
