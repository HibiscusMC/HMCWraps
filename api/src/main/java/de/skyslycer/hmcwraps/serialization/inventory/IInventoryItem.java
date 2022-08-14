package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import javax.annotation.Nullable;

public interface IInventoryItem extends ISerializableItem {

    @Nullable
    Action getAction();

}
