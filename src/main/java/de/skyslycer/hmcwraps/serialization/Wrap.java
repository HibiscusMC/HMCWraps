package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap extends SerializableItem implements IWrap {

    private @Nullable String permission;
    private Boolean preview = true;
    private String uuid;
    private @Nullable PhysicalWrap physical;

    @Override
    @Nullable
    public String getPermission() {
        return permission;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    @Nullable
    public IPhysicalWrap getPhysical() {
        return physical;
    }

    @Override
    public Boolean isPreview() {
        return preview;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null) {
            return true;
        }
        return sender.hasPermission(getPermission());
    }

}
