package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class WarehouseAlreadyExistsException extends BusinessException {

    public WarehouseAlreadyExistsException(String businessUnitCode) {
        super("Warehouse businessUnitCode already exists: " + businessUnitCode);
    }
}

