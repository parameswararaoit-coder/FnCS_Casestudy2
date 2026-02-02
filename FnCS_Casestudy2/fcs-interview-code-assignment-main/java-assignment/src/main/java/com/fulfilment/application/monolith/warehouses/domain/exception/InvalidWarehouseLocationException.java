package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class InvalidWarehouseLocationException extends BusinessException {

    public InvalidWarehouseLocationException(String location) {
        super("Invalid warehouse location: " + location);
    }
}

