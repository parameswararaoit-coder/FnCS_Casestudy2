package com.fulfilment.application.monolith.api.exception;

public class ProductNameMissingException extends BusinessException {

    public ProductNameMissingException() {
        super("Product Name was not set on request.");
    }
}

