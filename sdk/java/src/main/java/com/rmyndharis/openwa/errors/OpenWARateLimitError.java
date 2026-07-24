package com.rmyndharis.idawhats.errors;

/** 429 Too Many Requests — rate limited. */
public class IdaWhatsRateLimitError extends IdaWhatsApiError {
    public IdaWhatsRateLimitError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
