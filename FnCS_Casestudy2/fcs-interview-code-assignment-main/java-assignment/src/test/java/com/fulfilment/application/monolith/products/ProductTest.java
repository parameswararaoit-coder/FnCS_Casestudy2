package com.fulfilment.application.monolith.products;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    @Test
    void constructorsInitializeFields() {
        Product named = new Product("CHAIR");
        assertEquals("CHAIR", named.name);

        Product empty = new Product();
        empty.name = "TABLE";
        empty.stock = 5;

        assertEquals("TABLE", empty.name);
        assertEquals(5, empty.stock);
    }
}
