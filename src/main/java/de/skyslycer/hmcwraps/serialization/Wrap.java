package de.skyslycer.hmcwraps.serialization;

import java.util.List;
import javax.annotation.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap {

    private String permission;
    private String id;
    private String name;
    private boolean glow;
    private List<String> lore;
    private boolean preview;
    private String uuid;
    private @Nullable
    PhysicalWrap physical;

    public String getPermission() {
        return permission;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public boolean isPreview() {
        return preview;
    }

    public boolean isGlow() {
        return glow;
    }

    public String getUuid() {
        return uuid;
    }

    @Nullable
    public PhysicalWrap getPhysical() {
        return physical;
    }

}
