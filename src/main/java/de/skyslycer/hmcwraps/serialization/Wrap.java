package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import javax.annotation.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap extends SerializableItem {

    private @Nullable String permission;
    private @Nullable Boolean preview;
    private String uuid;
    private @Nullable PhysicalWrap physical;

    @Nullable
    public String getPermission() {
        return permission;
    }

    public String getUuid() {
        return uuid;
    }

    @Nullable
    public PhysicalWrap getPhysical() {
        return physical;
    }

    @Nullable
    public Boolean isPreview() {
        return preview;
    }

}
