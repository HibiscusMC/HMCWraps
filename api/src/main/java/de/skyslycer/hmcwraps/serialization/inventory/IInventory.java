package de.skyslycer.hmcwraps.serialization.inventory;

import java.util.Map;

public interface IInventory {

    boolean isOpenShortcut();

    String getTitle();

    int getRows();

    int getTargetItemSlot();

    Map<Integer, IInventoryItem> getItems();

    InventoryType getType();

}
