package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.LocationCapacityExceededException;
import com.fulfilment.application.monolith.warehouses.domain.exception.MaxWarehousesReachedException;
import com.fulfilment.application.monolith.warehouses.domain.exception.WarehouseAlreadyExistsException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {

        WarehouseUseCaseSupport.validateRequiredFields(warehouse);
        WarehouseUseCaseSupport.normalizeWarehouse(warehouse);

        // Business Unit Code must be unique
        Warehouse existing =
                warehouseStore.findAnyByBusinessUnitCode(warehouse.businessUnitCode);
        if (existing != null) {
            throw new WarehouseAlreadyExistsException(warehouse.businessUnitCode);
        }

        // Location must exist
        Location location =
                WarehouseUseCaseSupport.requireLocation(locationResolver, warehouse);

        WarehouseUseCaseSupport.validateCapacityAndStock(warehouse, location);

        List<Warehouse> activeWarehouses = warehouseStore.getAll();
        long activeCountAtLocation =
                WarehouseUseCaseSupport.countActiveAtLocation(
                        activeWarehouses, warehouse.location);

        if (activeCountAtLocation >= location.maxNumberOfWarehouses) {
            throw new MaxWarehousesReachedException(warehouse.location);
        }

        int totalCapacityAtLocation =
                WarehouseUseCaseSupport.sumCapacityAtLocation(
                        activeWarehouses, warehouse.location);

        if (totalCapacityAtLocation + warehouse.capacity > location.maxCapacity) {
            throw new LocationCapacityExceededException(warehouse.location);
        }

        warehouse.createdAt = LocalDateTime.now();
        warehouse.archivedAt = null;

        warehouseStore.create(warehouse);
    }

}
