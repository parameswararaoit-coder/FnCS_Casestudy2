package com.fulfilment.application.monolith.api.exception;

public class WarehouseNotFoundException extends BusinessException {
    public WarehouseNotFoundException(String buCode) {
        super("Active warehouse not found: " + buCode);
    }
}
