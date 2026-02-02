package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.api.exception.*;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class FulfilmentServiceTest {

    @Inject
    FulfilmentService service;
    @Inject
    FulfilmentRepository assignmentRepo;
    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void clean() {
        assignmentRepo.deleteAll();
        em.createQuery("delete from Product p where p.name like 'TEST_%'").executeUpdate();
        Store.delete("name like ?1", "TEST_%");
        em.createQuery("delete from DbWarehouse w where w.businessUnitCode like 'TEST_%'")
                .executeUpdate();
        em.flush();
    }

    @Test
    void shouldRejectInvalidStoreId() {
        InvalidInputException ex =
                assertThrows(
                        InvalidInputException.class,
                        () -> service.assign(null, 1L, "W1")
                );

        assertEquals("storeId is invalid.", ex.getMessage());
    }

    @Test
    void shouldRejectInvalidProductId() {
        InvalidInputException ex =
                assertThrows(
                        InvalidInputException.class,
                        () -> service.assign(1L, 0L, "W1")
                );

        assertEquals("productId is invalid.", ex.getMessage());
    }

    @Test
    void shouldRejectInvalidWarehouseBuCode() {
        InvalidInputException ex =
                assertThrows(
                        InvalidInputException.class,
                        () -> service.assign(1L, 1L, "  ")
                );

        assertEquals("warehouseBuCode is invalid.", ex.getMessage());
    }

    @Test
    @Transactional
    void shouldRejectUnknownStore() {
        Long productId = createProduct("P1");
        String warehouseBu = createWarehouse("W1");

        assertThrows(
                StoreNotFoundException.class,
                () -> service.assign(999L, productId, warehouseBu)
        );
    }

    @Test
    @Transactional
    void shouldRejectUnknownProduct() {
        Long storeId = createStore("S1");
        String warehouseBu = createWarehouse("W1");

        assertThrows(
                ProductNotFoundException.class,
                () -> service.assign(storeId, 999L, warehouseBu)
        );
    }

    @Test
    @Transactional
    void shouldRejectUnknownWarehouse() {
        Long storeId = createStore("S1");
        Long productId = createProduct("P1");

        assertThrows(
                WarehouseNotFoundException.class,
                () -> service.assign(storeId, productId, "TEST_W1")
        );
    }

    @Test
    @Transactional
    void shouldRejectDuplicateAssignment() {
        Long storeId = createStore("S1");
        Long productId = createProduct("P1");
        String warehouseBu = createWarehouse("W1");

        service.assign(storeId, productId, warehouseBu);

        assertThrows(
                AssignmentAlreadyExistsException.class,
                () -> service.assign(storeId, productId, warehouseBu)
        );
    }

    @Test
    @Transactional
    void shouldEnforceMaxTwoWarehousesPerStoreProduct() {
        Long storeId = createStore("S1");
        Long productId = createProduct("P1");
        String warehouse1 = createWarehouse("W1");
        String warehouse2 = createWarehouse("W2");
        String warehouse3 = createWarehouse("W3");

        service.assign(storeId, productId, warehouse1);
        service.assign(storeId, productId, warehouse2);

        assertThrows(
                MaxWarehousesPerStoreProductExceededException.class,
                () -> service.assign(storeId, productId, warehouse3)
        );
    }

    @Test
    @Transactional
    void shouldEnforceMaxThreeWarehousesPerStore() {
        Long storeId = createStore("S1");
        Long p1 = createProduct("P1");
        Long p2 = createProduct("P2");
        Long p3 = createProduct("P3");
        Long p4 = createProduct("P4");
        String w1 = createWarehouse("W1");
        String w2 = createWarehouse("W2");
        String w3 = createWarehouse("W3");
        String w4 = createWarehouse("W4");

        service.assign(storeId, p1, w1);
        service.assign(storeId, p2, w2);
        service.assign(storeId, p3, w3);

        assertThrows(
                MaxWarehousesPerStoreExceededException.class,
                () -> service.assign(storeId, p4, w4)
        );
    }

    @Transactional
    Long createStore(String name) {
        Store s = new Store();
        s.name = "TEST_" + name;
        s.quantityProductsInStock = 0;
        s.persist();
        em.flush();
        return s.id;
    }

    @Transactional
    Long createProduct(String name) {
        Product p = new Product();
        p.name = "TEST_" + name;
        p.description = "d";
        p.price = BigDecimal.TEN;
        p.stock = 1;
        em.persist(p);
        em.flush();
        return p.id;
    }

    @Transactional
    String createWarehouse(String buCode) {
        DbWarehouse w = new DbWarehouse();
        w.businessUnitCode = "TEST_" + buCode;
        w.location = "AMSTERDAM-001";
        w.capacity = 50;
        w.stock = 10;
        w.createdAt = LocalDateTime.now();
        w.archivedAt = null;
        em.persist(w);
        em.flush();
        return w.businessUnitCode;
    }

}