package com.fulfilment.application.monolith.api.exception;

public class StoreNotFoundException extends BusinessException {
    public StoreNotFoundException(Long storeId) {
        super("Store not found: " + storeId);
    }
}
