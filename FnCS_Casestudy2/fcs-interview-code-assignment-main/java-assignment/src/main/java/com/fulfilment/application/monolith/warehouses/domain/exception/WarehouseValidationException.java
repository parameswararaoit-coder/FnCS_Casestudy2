package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class WarehouseValidationException extends BusinessException {
    public WarehouseValidationException(String message) {
        super(message);
    }
}
