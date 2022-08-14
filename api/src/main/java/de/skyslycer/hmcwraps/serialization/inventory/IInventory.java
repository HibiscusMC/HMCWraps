package de.skyslycer.hmcwraps.serialization.inventory;

import java.util.Map;

public interface IInventory {

    String getTitle();

    int getRows();

    int getTargetItemSlot();

    Map<Integer, ? extends IInventoryItem> getItems();

    InventoryType getType();

}
