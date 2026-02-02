package com.fulfilment.application.monolith.api.exception;

public class LegacyStoreWriteException extends TechnicalException {

    public LegacyStoreWriteException(String storeName, Throwable cause) {
        super("Failed to write store data to legacy system for store: " + storeName, cause);
    }
}

