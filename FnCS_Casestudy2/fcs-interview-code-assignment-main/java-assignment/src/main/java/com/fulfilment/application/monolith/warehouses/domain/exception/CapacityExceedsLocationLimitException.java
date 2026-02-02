package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class CapacityExceedsLocationLimitException extends BusinessException {

    public CapacityExceedsLocationLimitException() {
        super("Warehouse capacity cannot exceed location max capacity.");
    }
}
