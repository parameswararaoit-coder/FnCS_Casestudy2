package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.*;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void replace(Warehouse newWarehouse) {

        WarehouseUseCaseSupport.validateRequiredFields(newWarehouse);
        WarehouseUseCaseSupport.normalizeWarehouse(newWarehouse);

        Warehouse current =
                warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

        if (current == null) {
            throw new ActiveWarehouseNotFoundException(newWarehouse.businessUnitCode);
        }

        // Location must exist
        Location targetLocation =
                WarehouseUseCaseSupport.requireLocation(locationResolver, newWarehouse);

        // --- replacement validations ---

        int currentStock = current.stock == null ? 0 : current.stock;

        // 1) New capacity must accommodate old stock
        if (newWarehouse.capacity < currentStock) {
            throw new CapacityBelowExistingStockException();
        }

        // 2) Stock must match previous warehouse
        if (!newWarehouse.stock.equals(current.stock)) {
            throw new StockMismatchOnReplaceException();
        }

        List<Warehouse> activeWarehouses = warehouseStore.getAll();
        boolean movingLocation =
                !newWarehouse.location.equals(current.location);

        long countAtTarget =
                WarehouseUseCaseSupport.countActiveAtLocation(
                        activeWarehouses, newWarehouse.location);

        if (movingLocation && countAtTarget >= targetLocation.maxNumberOfWarehouses) {
            throw new MaxWarehousesReachedException(newWarehouse.location);
        }

        // Single warehouse cannot exceed location cap
        WarehouseUseCaseSupport.validateCapacityNotExceedingLocation(
                newWarehouse, targetLocation);

        int sumCapacityAtTarget =
                WarehouseUseCaseSupport.sumCapacityAtLocation(
                        activeWarehouses, newWarehouse.location);

        int currentCap = current.capacity == null ? 0 : current.capacity;

        int resultingCapacityAtTarget = movingLocation
                ? sumCapacityAtTarget + newWarehouse.capacity
                : sumCapacityAtTarget - currentCap + newWarehouse.capacity;

        if (resultingCapacityAtTarget > targetLocation.maxCapacity) {
            throw new LocationCapacityExceededException(newWarehouse.location);
        }

        // --- archive + create (history) ---

        LocalDateTime now = LocalDateTime.now();

        current.archivedAt = now;
        warehouseStore.update(current);

        Warehouse created = new Warehouse();
        created.businessUnitCode = newWarehouse.businessUnitCode;
        created.location = newWarehouse.location;
        created.capacity = newWarehouse.capacity;
        created.stock = newWarehouse.stock;
        created.createdAt = now;
        created.archivedAt = null;

        warehouseStore.create(created);
    }

}
