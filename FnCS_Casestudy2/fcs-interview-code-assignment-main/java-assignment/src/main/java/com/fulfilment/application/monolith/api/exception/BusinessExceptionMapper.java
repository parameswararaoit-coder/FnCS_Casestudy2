package com.fulfilment.application.monolith.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.warehouses.domain.exception.WarehouseValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper
        implements ExceptionMapper<BusinessException> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Response toResponse(BusinessException ex) {

        int status = mapStatus(ex);

        ObjectNode json = OBJECT_MAPPER.createObjectNode();
        json.put("error", ex.getMessage());
        json.put("status", status);
        json.put("type", ex.getClass().getSimpleName());

        return Response.status(status)
                .entity(json)
                .build();
    }

    private int mapStatus(BusinessException ex) {
        if (ex instanceof InvalidInputException
                || ex instanceof WarehouseValidationException) {
            return 422;
        }
        if (ex instanceof StoreNotFoundException
                || ex instanceof ProductNotFoundException
                || ex instanceof WarehouseNotFoundException) {
            return 404;
        }
        return 409;
    }
}


