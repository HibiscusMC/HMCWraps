package de.skyslycer.hmcwraps.serialization;

import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrappableItem implements IWrappableItem {

    private Map<String, Wrap> wraps;

    @Override
    public Map<String, ? extends IWrap> getWraps() {
        return wraps;
    }

}
