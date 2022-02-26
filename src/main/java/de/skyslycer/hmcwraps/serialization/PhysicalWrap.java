package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PhysicalWrap extends SerializableItem {

    private boolean keepAfterUnwrap;

    public boolean isKeepAfterUnwrap() {
        return keepAfterUnwrap;
    }

}
