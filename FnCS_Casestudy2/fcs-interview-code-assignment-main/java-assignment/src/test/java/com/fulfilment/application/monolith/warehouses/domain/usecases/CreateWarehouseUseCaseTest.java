package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.*;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CreateWarehouseUseCaseTest {

    @Test
    void createsWarehouseWhenValid() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        LocationResolver resolver = new MapLocationResolver(
                Map.of("NYC", new Location("NYC", 3, 500))
        );
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = " BU1 ";
        warehouse.location = " NYC ";
        warehouse.capacity = 200;
        warehouse.stock = 50;

        useCase.create(warehouse);

        assertEquals(1, store.created.size());
        Warehouse created = store.created.get(0);
        assertEquals("BU1", created.businessUnitCode);
        assertEquals("NYC", created.location);
        assertNotNull(created.createdAt);
        assertNull(created.archivedAt);
    }

    @Test
    void rejectsDuplicateBusinessUnitCode() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "BU1";
        store.warehouses.add(existing);

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, resolverNYC());

        Warehouse incoming = validWarehouse("BU1");

        assertThrows(
                WarehouseAlreadyExistsException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void rejectsWhenCapacityBelowStock() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new InMemoryWarehouseStore(), resolverNYC());

        Warehouse incoming = validWarehouse("BU1");
        incoming.capacity = 10;
        incoming.stock = 50;

        assertThrows(
                CapacityBelowStockException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void rejectsWhenMaxWarehousesReached() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(validWarehouse("BU1"));

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(
                        store,
                        new MapLocationResolver(Map.of("NYC", new Location("NYC", 1, 500)))
                );

        Warehouse incoming = validWarehouse("BU2");

        assertThrows(
                MaxWarehousesReachedException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void rejectsMissingRequestBody() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new InMemoryWarehouseStore(), resolverNYC());

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(null)
        );
    }

    @Test
    void rejectsMissingRequiredFields() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new InMemoryWarehouseStore(), resolverNYC());

        Warehouse missingBu = validWarehouse(" ");
        assertThrows(WarehouseValidationException.class, () -> useCase.create(missingBu));

        Warehouse missingLocation = validWarehouse("BU1");
        missingLocation.location = " ";
        assertThrows(WarehouseValidationException.class, () -> useCase.create(missingLocation));

        Warehouse missingCapacity = validWarehouse("BU2");
        missingCapacity.capacity = null;
        assertThrows(WarehouseValidationException.class, () -> useCase.create(missingCapacity));

        Warehouse missingStock = validWarehouse("BU3");
        missingStock.stock = null;
        assertThrows(WarehouseValidationException.class, () -> useCase.create(missingStock));
    }

    @Test
    void rejectsInvalidLocation() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(
                        new InMemoryWarehouseStore(),
                        new MapLocationResolver(Map.of("NYC", new Location("NYC", 3, 500)))
                );

        Warehouse incoming = validWarehouse("BU1");
        incoming.location = "MISSING";

        assertThrows(
                InvalidWarehouseLocationException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void createsWhenExistingCapacityIsNull() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "BU1";
        existing.location = "NYC";
        existing.capacity = null;
        existing.stock = 10;
        store.warehouses.add(existing);

        LocationResolver resolver = new MapLocationResolver(
                Map.of("NYC", new Location("NYC", 3, 500))
        );
        CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(store, resolver);

        Warehouse incoming = new Warehouse();
        incoming.businessUnitCode = "BU2";
        incoming.location = "NYC";
        incoming.capacity = 100;
        incoming.stock = 10;

        useCase.create(incoming);

        assertEquals(1, store.created.size());
        assertEquals("BU2", store.created.get(0).businessUnitCode);
    }

    @Test
    void rejectsCapacityExceedingLocationLimit() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(
                        new InMemoryWarehouseStore(),
                        new MapLocationResolver(Map.of("NYC", new Location("NYC", 3, 100)))
                );

        Warehouse incoming = validWarehouse("BU1");
        incoming.capacity = 150;

        assertThrows(
                CapacityExceedsLocationLimitException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void rejectsWhenTotalCapacityWouldExceedLocation() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse existing = validWarehouse("BU1");
        existing.capacity = 450;
        store.warehouses.add(existing);

        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(store, resolverNYC());

        Warehouse incoming = validWarehouse("BU2");
        incoming.capacity = 100;

        assertThrows(
                LocationCapacityExceededException.class,
                () -> useCase.create(incoming)
        );
    }

    @Test
    void rejectsInvalidCapacityOrStockValues() {
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(new InMemoryWarehouseStore(), resolverNYC());

        Warehouse invalidCapacity = validWarehouse("BU1");
        invalidCapacity.capacity = 0;
        assertThrows(WarehouseValidationException.class, () -> useCase.create(invalidCapacity));

        Warehouse invalidStock = validWarehouse("BU2");
        invalidStock.stock = -1;
        assertThrows(WarehouseValidationException.class, () -> useCase.create(invalidStock));
    }


    private static final class MapLocationResolver implements LocationResolver {

        private final Map<String, Location> locations;

        private MapLocationResolver(Map<String, Location> locations) {
            this.locations = locations;
        }

        @Override
        public Location resolveByIdentifier(String identifier) {
            return locations.get(identifier);
        }
    }

    private static final class InMemoryWarehouseStore implements WarehouseStore {

        private final List<Warehouse> warehouses = new ArrayList<>();
        private final List<Warehouse> created = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return new ArrayList<>(warehouses);
        }

        @Override
        public void create(Warehouse warehouse) {
            warehouses.add(warehouse);
            created.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            // no-op for tests
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.remove(warehouse);
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(warehouse -> warehouse.businessUnitCode.equals(buCode))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Warehouse findAnyByBusinessUnitCode(String buCode) {
            return findByBusinessUnitCode(buCode);
        }
    }

    private Warehouse validWarehouse(String bu) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = bu;
        w.location = "NYC";
        w.capacity = 100;
        w.stock = 10;
        return w;
    }

    private LocationResolver resolverNYC() {
        return new MapLocationResolver(
                Map.of("NYC", new Location("NYC", 3, 500))
        );
    }

}
