package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Inventory {

    private String title;
    private int rows;
    private int targetItemSlot;
    private Map<Integer, InventoryItem> items;

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getTargetItemSlot() {
        return targetItemSlot;
    }

    public Map<Integer, InventoryItem> getItems() {
        return items;
    }

}
