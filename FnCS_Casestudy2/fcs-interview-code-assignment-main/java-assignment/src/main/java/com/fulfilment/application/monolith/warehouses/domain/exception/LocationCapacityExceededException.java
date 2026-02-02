package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class LocationCapacityExceededException extends BusinessException {

    public LocationCapacityExceededException(String location) {
        super("Location capacity exceeded for location: " + location);
    }
}
