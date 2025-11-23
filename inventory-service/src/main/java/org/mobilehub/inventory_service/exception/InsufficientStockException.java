package org.mobilehub.inventory_service.exception;

import org.mobilehub.shared.common.events.InventoryRejectedEvent;

import java.util.List;

public class InsufficientStockException extends RuntimeException {
    private final List<InventoryRejectedEvent.Missing> missing;

    public InsufficientStockException(String message, List<InventoryRejectedEvent.Missing> missing) {
        super(message);
        this.missing = missing;
    }

    public List<InventoryRejectedEvent.Missing> getMissing() {
        return missing;
    }
}
