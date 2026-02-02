package com.fulfilment.application.monolith.api.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TechnicalExceptionMapper
        implements ExceptionMapper<TechnicalException> {

    @Override
    public Response toResponse(TechnicalException ex) {
        return Response.status(502) // Bad Gateway
                .entity("{\"error\":\"Legacy system unavailable\"}")
                .build();
    }
}

