package com.fulfilment.application.monolith.api.exception;

public class ProductIdProvidedOnCreateException extends BusinessException {

    public ProductIdProvidedOnCreateException() {
        super("Id was invalidly set on request.");
    }
}
