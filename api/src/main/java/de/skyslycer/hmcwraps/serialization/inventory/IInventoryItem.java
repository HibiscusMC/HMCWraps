package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import org.jetbrains.annotations.Nullable;

public interface IInventoryItem extends ISerializableItem {

    @Nullable
    Action getAction();

}
