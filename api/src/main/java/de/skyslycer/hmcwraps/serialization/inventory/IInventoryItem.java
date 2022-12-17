package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface IInventoryItem extends ISerializableItem {

    @Nullable
    HashMap<String, HashMap<String, List<String>>> getActions();

}
