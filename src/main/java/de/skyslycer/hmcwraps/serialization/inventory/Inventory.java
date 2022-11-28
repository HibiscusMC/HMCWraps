package de.skyslycer.hmcwraps.serialization.inventory;

import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Inventory implements IInventory {

    private boolean openShortcut;
    private String title;
    private InventoryType type;
    private int rows;
    private int targetItemSlot;
    private Map<Integer, InventoryItem> items;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getTargetItemSlot() {
        return targetItemSlot;
    }

    @Override
    public Map<Integer, ? extends IInventoryItem> getItems() {
        return items;
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    @Override
    public boolean isOpenShortcut() {
        return openShortcut;
    }

}
