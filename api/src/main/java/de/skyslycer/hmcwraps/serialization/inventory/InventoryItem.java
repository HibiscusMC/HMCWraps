package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class InventoryItem extends SerializableItem {

    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;
    private @Nullable List<Integer> fills;

    @Nullable
    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    @Nullable
    public List<Integer> getFills() {
        return fills;
    }

}
