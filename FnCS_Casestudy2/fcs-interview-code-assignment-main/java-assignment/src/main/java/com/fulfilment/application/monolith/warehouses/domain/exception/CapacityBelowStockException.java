package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class CapacityBelowStockException extends BusinessException {

    public CapacityBelowStockException() {
        super("Warehouse capacity must accommodate stock.");
    }
}
