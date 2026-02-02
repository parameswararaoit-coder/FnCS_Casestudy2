package com.fulfilment.application.monolith.warehouses.domain.exception;

import com.fulfilment.application.monolith.api.exception.BusinessException;

public class StockMismatchOnReplaceException extends BusinessException {

    public StockMismatchOnReplaceException() {
        super("New warehouse stock must match current warehouse stock.");
    }
}
