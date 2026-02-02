package com.fulfilment.application.monolith.fulfilment;

import com.fulfilment.application.monolith.api.exception.*;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

@ApplicationScoped
public class FulfilmentService {

    @Inject
    FulfilmentRepository repo;
    @Inject
    WarehouseRepository warehouseRepository;
    @Inject
    EntityManager em;

    public FulfilmentResponse assign(
            Long storeId, Long productId, String warehouseBuCode) {

        validateInputs(storeId, productId, warehouseBuCode);

        Store store = Store.findById(storeId);
        if (store == null) {
            throw new StoreNotFoundException(storeId);
        }

        Product product = em.find(Product.class, productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        DbWarehouse warehouse =
                warehouseRepository.findActiveDbByBusinessUnitCode(warehouseBuCode);
        if (warehouse == null) {
            throw new WarehouseNotFoundException(warehouseBuCode);
        }

        Long warehouseId = warehouse.id;

        // Duplicate assignment
        if (repo.existsAssignment(storeId, productId, warehouseId)) {
            throw new AssignmentAlreadyExistsException();
        }

        // Constraint 1: max 2 warehouses per store-product
        long whCountForStoreProduct =
                repo.countDistinctWarehousesForStoreProduct(storeId, productId);
        if (whCountForStoreProduct >= 2) {
            throw new MaxWarehousesPerStoreProductExceededException();
        }

        // Constraint 2: max 3 warehouses per store
        boolean warehouseAlreadyForStore =
                repo.isWarehouseAlreadyUsedByStore(storeId, warehouseId);
        if (!warehouseAlreadyForStore) {
            long distinctWhForStore =
                    repo.countDistinctWarehousesForStore(storeId);
            if (distinctWhForStore >= 3) {
                throw new MaxWarehousesPerStoreExceededException();
            }
        }

        // Constraint 3: max 5 product types per warehouse
        boolean productAlreadyForWarehouse =
                repo.isProductAlreadyUsedByWarehouse(warehouseId, productId);
        if (!productAlreadyForWarehouse) {
            long distinctProductsForWarehouse =
                    repo.countDistinctProductsForWarehouse(warehouseId);
            if (distinctProductsForWarehouse >= 5) {
                throw new MaxProductsPerWarehouseExceededException();
            }
        }

        Fulfilment assignment =
                new Fulfilment(storeId, productId, warehouseId, LocalDateTime.now());
        repo.persist(assignment);

        return new FulfilmentResponse(
                storeId,
                productId,
                warehouseBuCode.trim(),
                assignment.createdAt);
    }

    private void validateInputs(
            Long storeId, Long productId, String warehouseBuCode) {

        if (storeId == null || storeId <= 0) {
            throw new InvalidInputException("storeId is invalid.");
        }
        if (productId == null || productId <= 0) {
            throw new InvalidInputException("productId is invalid.");
        }
        if (warehouseBuCode == null || warehouseBuCode.isBlank()) {
            throw new InvalidInputException("warehouseBuCode is invalid.");
        }
    }

    public record FulfilmentResponse(
            Long storeId,
            Long productId,
            String warehouseBusinessUnitCode,
            LocalDateTime createdAt) {
    }
}
