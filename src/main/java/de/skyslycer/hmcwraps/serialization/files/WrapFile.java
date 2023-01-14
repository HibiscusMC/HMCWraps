package de.skyslycer.hmcwraps.serialization.files;

import de.skyslycer.hmcwraps.serialization.IWrapFile;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WrapFile extends Toggleable implements IWrapFile {

    private Map<String, WrappableItem> items = new HashMap<>();

    public WrapFile(Map<String, WrappableItem> items, boolean enabled) {
        super(enabled);
        this.items = items;
    }

    public WrapFile() {
    }

    @Override
    public Map<String, IWrappableItem> getItems() {
        return new HashMap<>(items);
    }

}
