package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public interface IInventoryItem extends ISerializableItem {

    @Nullable
    HashMap<String, HashMap<String, List<String>>> getActions();

}
