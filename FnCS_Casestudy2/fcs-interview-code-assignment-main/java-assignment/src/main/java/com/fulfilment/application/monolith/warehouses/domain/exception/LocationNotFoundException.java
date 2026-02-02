package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class LocationNotFoundException extends BusinessException {

    public LocationNotFoundException(String location) {
        super("Location does not exist: " + location);
    }
}

