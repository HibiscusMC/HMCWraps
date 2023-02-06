package de.skyslycer.hmcwraps.serialization.inventory;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Inventory {

    private boolean openShortcut;
    private String title;
    private Type type;
    private int rows;
    private int targetItemSlot;
    private Map<Integer, InventoryItem> items;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public Inventory(boolean openShortcut, String title, Type type, int rows, int targetItemSlot, Map<Integer, InventoryItem> items,
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

    public Type getType() {
        return type;
    }

    public boolean isOpenShortcut() {
        return openShortcut;
    }

    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    public enum Type {

        PAGINATED,
        SCROLLING

    }

}
