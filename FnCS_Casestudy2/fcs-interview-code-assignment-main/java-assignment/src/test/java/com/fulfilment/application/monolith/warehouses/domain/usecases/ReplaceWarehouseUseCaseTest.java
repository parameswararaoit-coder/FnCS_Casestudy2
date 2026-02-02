package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.*;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceWarehouseUseCaseTest {

    @Test
    void replacesWarehouseInSameLocation() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU1";
        current.location = "LOC1";
        current.capacity = 100;
        current.stock = 10;
        store.warehouses.add(current);

        Warehouse other = new Warehouse();
        other.businessUnitCode = "BU2";
        other.location = "LOC1";
        other.capacity = 50;
        other.stock = 5;
        store.warehouses.add(other);

        LocationResolver resolver = new MapLocationResolver(
                Map.of("LOC1", new Location("LOC1", 5, 500))
        );

        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = "LOC1";
        replacement.capacity = 120;
        replacement.stock = 10;

        useCase.replace(replacement);

        assertEquals(1, store.updated.size());
        assertNotNull(store.updated.get(0).archivedAt);
        assertEquals(1, store.created.size());
        assertEquals("BU1", store.created.get(0).businessUnitCode);
        assertEquals("LOC1", store.created.get(0).location);
    }

    @Test
    void rejectsWhenCurrentWarehouseMissing() {
        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        new InMemoryWarehouseStore(),
                        new MapLocationResolver(
                                Map.of("LOC1", new Location("LOC1", 5, 500))
                        )
                );

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = "LOC1";
        replacement.capacity = 120;
        replacement.stock = 10;

        assertThrows(
                ActiveWarehouseNotFoundException.class,
                () -> useCase.replace(replacement)
        );
    }


    @Test
    void replacesWhenMovingToNewLocation() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU1";
        current.location = "LOC1";
        current.capacity = 100;
        current.stock = 10;
        store.warehouses.add(current);

        Warehouse existingAtTarget = new Warehouse();
        existingAtTarget.businessUnitCode = "BU2";
        existingAtTarget.location = "LOC2";
        existingAtTarget.capacity = 50;
        existingAtTarget.stock = 5;
        store.warehouses.add(existingAtTarget);

        LocationResolver resolver = new MapLocationResolver(
                Map.of(
                        "LOC1", new Location("LOC1", 5, 500),
                        "LOC2", new Location("LOC2", 2, 200)
                )
        );

        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = "LOC2";
        replacement.capacity = 120;
        replacement.stock = 10;

        useCase.replace(replacement);

        assertEquals(1, store.updated.size());
        assertEquals(1, store.created.size());
        assertEquals("LOC2", store.created.get(0).location);
    }

    @Test
    void replacesWhenCurrentCapacityIsNull() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU1";
        current.location = "LOC1";
        current.capacity = null;
        current.stock = 10;
        store.warehouses.add(current);

        Warehouse other = new Warehouse();
        other.businessUnitCode = "BU2";
        other.location = "LOC1";
        other.capacity = 20;
        other.stock = 5;
        store.warehouses.add(other);

        LocationResolver resolver = new MapLocationResolver(
                Map.of("LOC1", new Location("LOC1", 5, 100))
        );

        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = "LOC1";
        replacement.capacity = 30;
        replacement.stock = 10;

        useCase.replace(replacement);

        assertEquals(1, store.updated.size());
        assertEquals(1, store.created.size());
    }

    @Test
    void rejectsWhenMovingToFullLocation() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 10));
        store.warehouses.add(warehouse("BU2", "LOC2", 50, 5));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        new MapLocationResolver(
                                Map.of(
                                        "LOC1", new Location("LOC1", 5, 500),
                                        "LOC2", new Location("LOC2", 1, 500)
                                )
                        )
                );

        Warehouse replacement = warehouse("BU1", "LOC2", 120, 10);

        assertThrows(
                MaxWarehousesReachedException.class,
                () -> useCase.replace(replacement)
        );
    }

    @Test
    void rejectsWhenStockWasNotPreviouslySet() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();

        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU1";
        current.location = "LOC1";
        current.capacity = 100;
        current.stock = null;
        store.warehouses.add(current);

        LocationResolver resolver =
                new MapLocationResolver(
                        Map.of("LOC1", new Location("LOC1", 5, 500))
                );

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(store, resolver);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = "LOC1";
        replacement.capacity = 120;
        replacement.stock = 10;

        StockMismatchOnReplaceException exception =
                assertThrows(
                        StockMismatchOnReplaceException.class,
                        () -> useCase.replace(replacement)
                );

        assertEquals(
                "New warehouse stock must match current warehouse stock.",
                exception.getMessage()
        );
    }


    @Test
    void rejectsWhenStockMismatch() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 10));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        new MapLocationResolver(
                                Map.of("LOC1", new Location("LOC1", 5, 500))
                        )
                );

        Warehouse replacement = warehouse("BU1", "LOC1", 120, 9);

        assertThrows(
                StockMismatchOnReplaceException.class,
                () -> useCase.replace(replacement)
        );
    }


    @Test
    void rejectsWhenLocationDoesNotExist() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 10));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(store, new MapLocationResolver(Map.of()));

        Warehouse replacement = warehouse("BU1", "MISSING", 120, 10);

        assertThrows(
                InvalidWarehouseLocationException.class,
                () -> useCase.replace(replacement)
        );
    }


    @Test
    void rejectsWhenNewCapacityBelowCurrentStock() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 25));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        new MapLocationResolver(
                                Map.of("LOC1", new Location("LOC1", 5, 500))
                        )
                );

        Warehouse replacement = warehouse("BU1", "LOC1", 20, 25);

        assertThrows(
                CapacityBelowExistingStockException.class,
                () -> useCase.replace(replacement)
        );
    }

    @Test
    void rejectsWhenCapacityExceedsLocationMaximum() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 10));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        new MapLocationResolver(
                                Map.of("LOC1", new Location("LOC1", 5, 150))
                        )
                );

        Warehouse replacement = warehouse("BU1", "LOC1", 200, 10);

        assertThrows(
                CapacityExceedsLocationLimitException.class,
                () -> useCase.replace(replacement)
        );
    }

    @Test
    void rejectsWhenResultingCapacityExceedsLocationLimit() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        store.warehouses.add(warehouse("BU1", "LOC1", 100, 10));
        store.warehouses.add(warehouse("BU2", "LOC1", 350, 10));

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        new MapLocationResolver(
                                Map.of("LOC1", new Location("LOC1", 5, 400))
                        )
                );

        Warehouse replacement = warehouse("BU1", "LOC1", 150, 10);

        assertThrows(
                LocationCapacityExceededException.class,
                () -> useCase.replace(replacement)
        );
    }

    @Test
    void rejectsInvalidCapacityOrStockValues() {
        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        new InMemoryWarehouseStore(),
                        new MapLocationResolver(Map.of())
                );

        Warehouse invalidCapacity = warehouse("BU1", "LOC1", 0, 10);
        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(invalidCapacity)
        );

        Warehouse invalidStock = warehouse("BU1", "LOC1", 10, -1);
        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(invalidStock)
        );
    }

    @Test
    void rejectsMissingBusinessUnitCode() {
        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        new InMemoryWarehouseStore(),
                        new MapLocationResolver(Map.of())
                );

        Warehouse replacement = warehouse(" ", "LOC1", 120, 10);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.replace(replacement)
        );
    }


    @Test
    void rejectsMissingLocation() {
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(
                new InMemoryWarehouseStore(),
                new MapLocationResolver(Map.of())
        );

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "BU1";
        replacement.location = " ";
        replacement.capacity = 120;
        replacement.stock = 10;

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> useCase.replace(replacement)
                );

        assertEquals(
                "Warehouse location was not set on request.",
                exception.getMessage()
        );
    }


    @Test
    void rejectsMissingCapacityOrStock() {
        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(
                new InMemoryWarehouseStore(),
                new MapLocationResolver(Map.of())
        );

        Warehouse missingCapacity = new Warehouse();
        missingCapacity.businessUnitCode = "BU1";
        missingCapacity.location = "LOC1";
        missingCapacity.capacity = null;
        missingCapacity.stock = 10;

        WarehouseValidationException capacityException =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> useCase.replace(missingCapacity)
                );

        assertEquals(
                "Warehouse capacity was not set on request.",
                capacityException.getMessage()
        );

        Warehouse missingStock = new Warehouse();
        missingStock.businessUnitCode = "BU1";
        missingStock.location = "LOC1";
        missingStock.capacity = 10;
        missingStock.stock = null;

        WarehouseValidationException stockException =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> useCase.replace(missingStock)
                );

        assertEquals(
                "Warehouse stock was not set on request.",
                stockException.getMessage()
        );
    }


    @Test
    void trimsBusinessUnitAndLocationBeforeLookup() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        Warehouse current = new Warehouse();
        current.businessUnitCode = "BU1";
        current.location = "LOC1";
        current.capacity = 100;
        current.stock = 10;
        store.warehouses.add(current);

        LocationResolver resolver = new MapLocationResolver(
                Map.of("LOC1", new Location("LOC1", 5, 500))
        );

        ReplaceWarehouseUseCase useCase = new ReplaceWarehouseUseCase(store, resolver);

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = " BU1 ";
        replacement.location = " LOC1 ";
        replacement.capacity = 120;
        replacement.stock = 10;

        useCase.replace(replacement);

        assertEquals(1, store.updated.size());
        assertEquals(1, store.created.size());
        assertEquals("LOC1", store.created.get(0).location);
        assertEquals("BU1", store.created.get(0).businessUnitCode);
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
        private final List<Warehouse> updated = new ArrayList<>();

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
            updated.add(warehouse);
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

    private Warehouse warehouse(
            String businessUnitCode,
            String location,
            Integer capacity,
            Integer stock
    ) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = businessUnitCode;
        w.location = location;
        w.capacity = capacity;
        w.stock = stock;
        return w;
    }

}
