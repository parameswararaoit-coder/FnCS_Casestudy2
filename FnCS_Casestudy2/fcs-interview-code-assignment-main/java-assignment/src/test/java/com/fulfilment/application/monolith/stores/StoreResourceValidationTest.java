package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class StoreResourceValidationTest {

    @Test
    void rejectsCreateWhenIdIsProvided() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"id\":10,\"name\":\"BAD\",\"quantityProductsInStock\":1}")
                .when()
                .post("/store")
                .then()
                .statusCode(500);
    }

    @Test
    void rejectsUpdateWhenNameMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"quantityProductsInStock\":5}")
                .when()
                .put("/store/1")
                .then()
                .statusCode(500);
    }

    @Test
    void rejectsPatchWhenNameMissing() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"quantityProductsInStock\":5}")
                .when()
                .patch("/store/1")
                .then()
                .statusCode(500);
    }
}
