package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
public class ProductEndpointCoverageTest {

    private static final String PATH = "product";

    @Test
    public void shouldCreateUpdateGetAndDeleteProduct() {
        Product product = new Product();
        product.name = "TEST_WIDGET";
        product.description = "Initial description";
        product.price = new BigDecimal("12.34");
        product.stock = 7;

        Long id =
                given()
                        .contentType("application/json")
                        .body(product)
                        .when()
                        .post(PATH)
                        .then()
                        .statusCode(201)
                        .body("name", equalTo("TEST_WIDGET"))
                        .extract()
                        .jsonPath()
                        .getLong("id");

        Product update = new Product();
        update.name = "TEST_WIDGET_UPDATED";
        update.description = "Updated description";
        update.price = new BigDecimal("45.00");
        update.stock = 11;

        given()
                .contentType("application/json")
                .body(update)
                .when()
                .put(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("TEST_WIDGET_UPDATED"))
                .body("description", equalTo("Updated description"))
                .body("stock", equalTo(11));

        given()
                .when()
                .get(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("TEST_WIDGET_UPDATED"));

        given()
                .when()
                .delete(PATH + "/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    public void shouldRejectCreateWithPresetId() {
        Product product = new Product();
        product.id = 99L;
        product.name = "TEST_BAD_ID";

        given()
                .contentType("application/json")
                .body(product)
                .when()
                .post(PATH)
                .then()
                .statusCode(409)
                .body("error", containsString("Id was invalidly set on request."))
                .body("status", equalTo(409))
                .body("type", equalTo("ProductIdProvidedOnCreateException"));
    }

    @Test
    public void shouldRejectUpdateWithoutName() {
        Product update = new Product();
        update.description = "Missing name";

        given()
                .contentType("application/json")
                .body(update)
                .when()
                .put(PATH + "/1")
                .then()
                .statusCode(409)
                .body("error", containsString("Product Name was not set on request."))
                .body("status", equalTo(409))
                .body("type", equalTo("ProductNameMissingException"));
    }

    @Test
    public void shouldReturnNotFoundForMissingProduct() {
        given()
                .when()
                .get(PATH + "/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("Product not found"))
                .body("status", equalTo(404))
                .body("type", equalTo("ProductNotFoundException"));

        given()
                .when()
                .delete(PATH + "/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("Product not found"))
                .body("status", equalTo(404))
                .body("type", equalTo("ProductNotFoundException"));
    }

}

