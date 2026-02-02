package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class WarehouseAlreadyArchivedException extends BusinessException {

    public WarehouseAlreadyArchivedException() {
        super("Warehouse is already archived.");
    }
}
