package de.skyslycer.hmcwraps.serialization.inventory;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Inventory implements IInventory {

    private boolean openShortcut;
    private String title;
    private InventoryType type;
    private int rows;
    private int targetItemSlot;
    private Map<Integer, InventoryItem> items;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public Inventory(boolean openShortcut, String title, InventoryType type, int rows, int targetItemSlot, Map<Integer, InventoryItem> items,
                     @Nullable HashMap<String, HashMap<String, List<String>>> actions) {
        this.openShortcut = openShortcut;
        this.title = title;
        this.type = type;
        this.rows = rows;
        this.targetItemSlot = targetItemSlot;
        this.items = items;
        this.actions = actions;
    }

    public Inventory() {
    }

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
    public Map<Integer, IInventoryItem> getItems() {
        return new HashMap<>(items);
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    @Override
    public boolean isOpenShortcut() {
        return openShortcut;
    }

    @Override
    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

}
