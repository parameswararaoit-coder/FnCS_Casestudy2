package com.fulfilment.application.monolith.api.exception;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId);
    }
}
