package com.fulfilment.application.monolith.api.exception;

public class StoreNameMissingException extends ApiException {

    public StoreNameMissingException() {
        super("Store Name was not set on request.");
    }

    @Override
    public int status() {
        return 422;
    }
}

