package de.skyslycer.hmcwraps.serialization.files;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WrapFile extends Toggleable {

    private Map<String, WrappableItem> items = new HashMap<>();

    private int config = 1;

    public WrapFile(Map<String, WrappableItem> items, boolean enabled) {
        super(enabled);
        this.items = items;
    }

    public WrapFile() {
    }

    public Map<String, WrappableItem> getItems() {
        return items;
    }

}
