package com.fulfilment.application.monolith.api.exception;

public class StoreIdProvidedOnCreateException extends ApiException {

    public StoreIdProvidedOnCreateException() {
        super("Id was invalidly set on request.");
    }

    @Override
    public int status() {
        return 422;
    }
}

