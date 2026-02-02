package com.fulfilment.application.monolith.api.exception;

public class MaxProductsPerWarehouseExceededException extends BusinessException {
    public MaxProductsPerWarehouseExceededException() {
        super("A warehouse can fulfil max 5 product types.");
    }
}
