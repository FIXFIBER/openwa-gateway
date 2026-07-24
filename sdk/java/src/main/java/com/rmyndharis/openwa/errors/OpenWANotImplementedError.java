package com.rmyndharis.idawhats.errors;

/** 501 Not Implemented — the active engine does not support this operation. */
public class IdaWhatsNotImplementedError extends IdaWhatsApiError {
    public IdaWhatsNotImplementedError(String message, int status, Object body, String errorKind) {
        super(message, status, body, errorKind);
    }
}
