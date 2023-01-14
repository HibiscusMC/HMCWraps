package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WrappableItem implements IWrappableItem {

    private Map<String, Wrap> wraps = new HashMap<>();

    public WrappableItem(Map<String, Wrap> wraps) {
        this.wraps = wraps;
    }

    public WrappableItem() {
    }

    @Override
    public Map<String, IWrap> getWraps() {
        return new HashMap<>(wraps);
    }

    public void putWrap(String name, Wrap wrap) {
        wraps.put(name, wrap);
    }

    public Map<String, Wrap> getWrapsPrivate() {
        return wraps;
    }

}
