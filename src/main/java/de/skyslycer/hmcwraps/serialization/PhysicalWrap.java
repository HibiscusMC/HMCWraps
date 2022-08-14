package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PhysicalWrap extends SerializableItem implements IPhysicalWrap {

    private boolean keepAfterUnwrap;

    @Override
    public boolean isKeepAfterUnwrap() {
        return keepAfterUnwrap;
    }

}
