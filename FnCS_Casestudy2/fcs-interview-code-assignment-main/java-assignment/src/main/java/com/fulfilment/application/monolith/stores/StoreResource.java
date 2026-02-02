package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.api.exception.StoreIdProvidedOnCreateException;
import com.fulfilment.application.monolith.api.exception.StoreNameMissingException;
import com.fulfilment.application.monolith.api.exception.StoreNotFoundException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

    private static final Logger LOGGER =
            Logger.getLogger(StoreResource.class.getName());

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Inject
    AfterCommitExecutor afterCommitExecutor;

    @GET
    public List<Store> get() {
        return Store.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Store getSingle(Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            throw new StoreNotFoundException(id);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Store store) {
        if (store.id != null) {
            throw new StoreIdProvidedOnCreateException();
        }

        store.persist();

        Store snapshot = snapshotOf(store);
        afterCommitExecutor.runAfterCommit(
                () -> legacyStoreManagerGateway.createStoreOnLegacySystem(snapshot));

        return Response.status(201).entity(store).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Store update(Long id, Store updatedStore) {
        if (updatedStore.name == null) {
            throw new StoreNameMissingException();
        }

        Store entity = Store.findById(id);
        if (entity == null) {
            throw new StoreNotFoundException(id);
        }

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

        Store snapshot = snapshotOf(entity);
        afterCommitExecutor.runAfterCommit(
                () -> legacyStoreManagerGateway.updateStoreOnLegacySystem(snapshot));

        return entity;
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Store patch(Long id, Store updatedStore) {
        if (updatedStore.name == null) {
            throw new StoreNameMissingException();
        }

        Store entity = Store.findById(id);
        if (entity == null) {
            throw new StoreNotFoundException(id);
        }

        entity.name = updatedStore.name;

        if (updatedStore.quantityProductsInStock != 0) {
            entity.quantityProductsInStock =
                    updatedStore.quantityProductsInStock;
        }

        Store snapshot = snapshotOf(entity);
        afterCommitExecutor.runAfterCommit(
                () -> legacyStoreManagerGateway.updateStoreOnLegacySystem(snapshot));

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            throw new StoreNotFoundException(id);
        }

        Store snapshot = snapshotOf(entity);
        entity.delete();

        afterCommitExecutor.runAfterCommit(
                () -> legacyStoreManagerGateway.updateStoreOnLegacySystem(snapshot));

        return Response.noContent().build();
    }

    private Store snapshotOf(Store source) {
        Store snapshot = new Store();
        snapshot.id = source.id;
        snapshot.name = source.name;
        snapshot.quantityProductsInStock =
                source.quantityProductsInStock;
        return snapshot;
    }
}

