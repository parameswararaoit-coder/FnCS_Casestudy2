package com.fulfilment.application.monolith.api.excption;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.api.exception.BusinessException;
import com.fulfilment.application.monolith.api.exception.BusinessExceptionMapper;
import com.fulfilment.application.monolith.api.exception.InvalidInputException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionMapperTest {

    @Test
    void mapsBusinessExceptionToHttpStatus() {
        BusinessExceptionMapper mapper = new BusinessExceptionMapper();

        BusinessException ex =
                new InvalidInputException("storeId is invalid.");

        Response response = mapper.toResponse(ex);
        ObjectNode entity = (ObjectNode) response.getEntity();

        assertEquals(422, response.getStatus());
        assertEquals("InvalidInputException", entity.get("type").asText());
        assertEquals(422, entity.get("status").asInt());
        assertEquals("storeId is invalid.", entity.get("error").asText());
    }
}

