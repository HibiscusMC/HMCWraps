package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap extends SerializableItem {

    private @Nullable String permission;
    private Boolean preview = true;
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

    public Boolean isPreview() {
        return preview;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null) {
            return true;
        }
        return sender.hasPermission(getPermission());
    }

}
