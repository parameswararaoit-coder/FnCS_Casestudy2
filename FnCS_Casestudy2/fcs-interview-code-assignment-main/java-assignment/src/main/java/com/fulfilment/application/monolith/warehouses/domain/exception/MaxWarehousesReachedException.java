package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class MaxWarehousesReachedException extends BusinessException {

    public MaxWarehousesReachedException(String location) {
        super("Max number of warehouses reached for location: " + location);
    }
}
