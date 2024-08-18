package de.skyslycer.hmcwraps.serialization.inventory;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Inventory {

    private List<String> sortOrder;
    private ShortcutSettings shortcut;
    private boolean itemChangeEnabled;
    private boolean openWithoutItemEnabled;
    private String title;
    private @Nullable String noItemTitle;
    private Type type;
    private int rows;
    private int targetItemSlot;
    private Map<String, InventoryItem> items;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getTargetItemSlot() {
        return targetItemSlot;
    }

    public Map<String, InventoryItem> getItems() {
        return items;
    }

    public Type getType() {
        return type;
    }

    public ShortcutSettings getShortcut() {
        return shortcut;
    }

    @Nullable
    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    public boolean isItemChangeEnabled() {
        return itemChangeEnabled;
    }

    public boolean isOpenWithoutItemEnabled() {
        return openWithoutItemEnabled;
    }

    @Nullable
    public String getNoItemTitle() {
        return noItemTitle;
    }

    public List<String> getSortOrder() {
        return sortOrder;
    }

    public enum Type {

        PAGINATED,
        SCROLLING

    }

}
