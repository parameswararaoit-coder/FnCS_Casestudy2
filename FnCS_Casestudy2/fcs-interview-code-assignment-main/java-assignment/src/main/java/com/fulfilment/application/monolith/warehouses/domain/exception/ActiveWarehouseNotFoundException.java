package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class ActiveWarehouseNotFoundException extends BusinessException {

    public ActiveWarehouseNotFoundException(String businessUnitCode) {
        super("Active warehouse not found for businessUnitCode=" + businessUnitCode);
    }
}

