package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class InventoryItem extends SerializableItem implements IInventoryItem {

    private @Nullable InventoryAction action;

    public InventoryItem(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags,
            @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments, @Nullable Integer amount,
            @Nullable InventoryAction action) {
        super(id, name, glow, lore, flags, modelId, enchantments, amount);
        this.action = action;
    }

    public InventoryItem() { }

    @Override
    @Nullable
    public InventoryAction getAction() {
        return action;
    }

}
