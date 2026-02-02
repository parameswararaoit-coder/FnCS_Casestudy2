package com.fulfilment.application.monolith.api.exception;

public class AssignmentAlreadyExistsException extends BusinessException {
    public AssignmentAlreadyExistsException() {
        super("Assignment already exists.");
    }
}