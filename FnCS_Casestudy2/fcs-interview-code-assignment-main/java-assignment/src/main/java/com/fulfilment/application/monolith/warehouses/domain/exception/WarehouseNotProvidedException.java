package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class WarehouseNotProvidedException extends BusinessException {

    public WarehouseNotProvidedException() {
        super("Warehouse was not provided.");
    }
}

