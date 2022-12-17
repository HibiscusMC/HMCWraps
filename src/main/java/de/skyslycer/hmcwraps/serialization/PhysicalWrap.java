package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PhysicalWrap extends SerializableItem implements IPhysicalWrap {

    private boolean keepAfterUnwrap;

    public PhysicalWrap(String id, String name, @Nullable Boolean glow,
            @Nullable List<String> lore, @Nullable List<String> flags,
            @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments,
            @Nullable Integer amount, boolean keepAfterUnwrap) {
        super(id, name, glow, lore, flags, modelId, enchantments, amount);
        this.keepAfterUnwrap = keepAfterUnwrap;
    }

    public PhysicalWrap() { }

    @Override
    public boolean isKeepAfterUnwrap() {
        return keepAfterUnwrap;
    }

}
