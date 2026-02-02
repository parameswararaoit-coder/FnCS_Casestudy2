package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.CapacityBelowStockException;
import com.fulfilment.application.monolith.warehouses.domain.exception.CapacityExceedsLocationLimitException;
import com.fulfilment.application.monolith.warehouses.domain.exception.InvalidWarehouseLocationException;
import com.fulfilment.application.monolith.warehouses.domain.exception.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;
import java.util.Objects;

final class WarehouseUseCaseSupport {

    private WarehouseUseCaseSupport() {
    }

    static void validateRequiredFields(Warehouse warehouse) {

        if (warehouse == null) {
            throw new WarehouseValidationException("Request body was not set.");
        }
        if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
            throw new WarehouseValidationException(
                    "Warehouse businessUnitCode was not set on request.");
        }
        if (warehouse.location == null || warehouse.location.isBlank()) {
            throw new WarehouseValidationException(
                    "Warehouse location was not set on request.");
        }
        if (warehouse.capacity == null) {
            throw new WarehouseValidationException(
                    "Warehouse capacity was not set on request.");
        }
        if (warehouse.stock == null) {
            throw new WarehouseValidationException(
                    "Warehouse stock was not set on request.");
        }
        if (warehouse.capacity <= 0) {
            throw new WarehouseValidationException(
                    "Warehouse capacity must be > 0.");
        }
        if (warehouse.stock < 0) {
            throw new WarehouseValidationException(
                    "Warehouse stock must be >= 0.");
        }
    }

    static void normalizeWarehouse(Warehouse warehouse) {
        warehouse.businessUnitCode = warehouse.businessUnitCode.trim();
        warehouse.location = warehouse.location.trim();
    }

    static Location requireLocation(
            LocationResolver locationResolver, Warehouse warehouse) {

        Location location =
                locationResolver.resolveByIdentifier(warehouse.location);

        if (location == null) {
            throw new InvalidWarehouseLocationException(warehouse.location);
        }
        return location;
    }

    static void validateCapacityAndStock(
            Warehouse warehouse, Location location) {

        if (warehouse.capacity < warehouse.stock) {
            throw new CapacityBelowStockException();
        }
        validateCapacityNotExceedingLocation(warehouse, location);
    }

    static void validateCapacityNotExceedingLocation(
            Warehouse warehouse, Location location) {

        if (warehouse.capacity > location.maxCapacity) {
            throw new CapacityExceedsLocationLimitException();
        }
    }

    static long countActiveAtLocation(
            List<Warehouse> activeWarehouses, String location) {

        return activeWarehouses.stream()
                .filter(w -> location.equals(w.location))
                .count();
    }

    static int sumCapacityAtLocation(
            List<Warehouse> activeWarehouses, String location) {

        return activeWarehouses.stream()
                .filter(w -> location.equals(w.location))
                .map(w -> w.capacity)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();
    }
}

