package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.api.exception.LegacyStoreWriteException;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
public class LegacyStoreManagerGateway {

    public void createStoreOnLegacySystem(Store store) {
        writeToFile(store);
    }

    public void updateStoreOnLegacySystem(Store store) {
        writeToFile(store);
    }

    private void writeToFile(Store store) {
        try {
            Path tempFile = Files.createTempFile(store.name, ".txt");

            String content =
                    "Store data [ name ="
                            + store.name
                            + " ] [ items on stock ="
                            + store.quantityProductsInStock
                            + "]";

            Files.write(tempFile, content.getBytes());

            // optional verification
            Files.readAllBytes(tempFile);

            Files.delete(tempFile);

        } catch (Exception e) {
            throw new LegacyStoreWriteException(store.name, e);
        }
    }
}
