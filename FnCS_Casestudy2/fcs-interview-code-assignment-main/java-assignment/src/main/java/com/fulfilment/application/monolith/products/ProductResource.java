package com.fulfilment.application.monolith.products;

import com.fulfilment.application.monolith.api.exception.ProductIdProvidedOnCreateException;
import com.fulfilment.application.monolith.api.exception.ProductNameMissingException;
import com.fulfilment.application.monolith.api.exception.ProductNotFoundException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("product")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @GET
    public List<Product> get() {
        return productRepository.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Product getSingle(Long id) {
        Product entity = productRepository.findById(id);
        if (entity == null) {
            throw new ProductNotFoundException(id);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Product product) {
        if (product.id != null) {
            throw new ProductIdProvidedOnCreateException();
        }

        productRepository.persist(product);
        return Response.status(201).entity(product).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Product update(Long id, Product product) {
        if (product.name == null) {
            throw new ProductNameMissingException();
        }

        Product entity = productRepository.findById(id);
        if (entity == null) {
            throw new ProductNotFoundException(id);
        }

        entity.name = product.name;
        entity.description = product.description;
        entity.price = product.price;
        entity.stock = product.stock;

        productRepository.persist(entity);
        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        Product entity = productRepository.findById(id);
        if (entity == null) {
            throw new ProductNotFoundException(id);
        }

        productRepository.delete(entity);
        return Response.noContent().build();
    }
}
