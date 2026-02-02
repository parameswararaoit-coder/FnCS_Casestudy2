package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class CapacityBelowExistingStockException extends BusinessException {

    public CapacityBelowExistingStockException() {
        super("New capacity must accommodate existing stock.");
    }
}

