package com.fulfilment.application.monolith.api.exception;

public abstract class TechnicalException extends RuntimeException {

    protected TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
