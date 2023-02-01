package de.skyslycer.hmcwraps.serialization.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IInventory {

    boolean isOpenShortcut();

    String getTitle();

    int getRows();

    int getTargetItemSlot();

    Map<Integer, IInventoryItem> getItems();

    InventoryType getType();

    HashMap<String, HashMap<String, List<String>>> getActions();
}
