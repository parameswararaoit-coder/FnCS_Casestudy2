package com.fulfilment.application.monolith.api.exception;

public class MaxWarehousesPerStoreProductExceededException extends BusinessException {
    public MaxWarehousesPerStoreProductExceededException() {
        super("A product can be fulfilled by max 2 warehouses per store.");
    }
}
