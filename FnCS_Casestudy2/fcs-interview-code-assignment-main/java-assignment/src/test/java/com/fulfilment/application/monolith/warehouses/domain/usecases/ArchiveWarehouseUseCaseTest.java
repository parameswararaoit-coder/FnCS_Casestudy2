package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exception.WarehouseAlreadyArchivedException;
import com.fulfilment.application.monolith.warehouses.domain.exception.WarehouseNotProvidedException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ArchiveWarehouseUseCaseTest {

    @Test
    void archivesWarehouse() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(store);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";

        useCase.archive(warehouse);

        assertEquals(1, store.updated.size());
        assertNotNull(store.updated.get(0).archivedAt);
    }

    @Test
    void rejectsAlreadyArchivedWarehouse() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(store);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU1";
        warehouse.archivedAt = LocalDateTime.now();

        WarehouseAlreadyArchivedException ex =
                assertThrows(
                        WarehouseAlreadyArchivedException.class,
                        () -> useCase.archive(warehouse)
                );

        assertEquals("Warehouse is already archived.", ex.getMessage());
    }

    @Test
    void rejectsMissingWarehouse() {
        InMemoryWarehouseStore store = new InMemoryWarehouseStore();
        ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(store);

        WarehouseNotProvidedException ex =
                assertThrows(
                        WarehouseNotProvidedException.class,
                        () -> useCase.archive(null)
                );

        assertEquals("Warehouse was not provided.", ex.getMessage());
    }

    // ---------------------------------------------------------------------

    private static final class InMemoryWarehouseStore implements WarehouseStore {

        private final List<Warehouse> updated = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return List.of();
        }

        @Override
        public void create(Warehouse warehouse) {
            // no-op for tests
        }

        @Override
        public void update(Warehouse warehouse) {
            updated.add(warehouse);
        }

        @Override
        public void remove(Warehouse warehouse) {
            // no-op for tests
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return null;
        }

        @Override
        public Warehouse findAnyByBusinessUnitCode(String buCode) {
            return null;
        }
    }
}

