package com.fulfilment.application.monolith.api.exception;

public class MaxWarehousesPerStoreExceededException extends BusinessException {
    public MaxWarehousesPerStoreExceededException() {
        super("A store can be fulfilled by max 3 warehouses.");
    }
}
