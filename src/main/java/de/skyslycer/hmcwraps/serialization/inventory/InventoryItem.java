package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import javax.annotation.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class InventoryItem extends SerializableItem {

    private @Nullable Action action;

    @Nullable
    public Action getAction() {
        return action;
    }

}
