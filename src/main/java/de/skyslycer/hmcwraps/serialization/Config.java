package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Config {

    private boolean openShortcut;
    private Inventory inventory;
    private SerializableItem unwrapper;
    private Map<String, WrappableItem> items;

    public boolean isOpenShortcut() {
        return openShortcut;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public SerializableItem getUnwrapper() {
        return unwrapper;
    }

    public Map<String, WrappableItem> getItems() {
        return items;
    }

}
