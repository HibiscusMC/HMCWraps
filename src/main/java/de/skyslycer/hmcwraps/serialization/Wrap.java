package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
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
    public Optional<IPhysicalWrap> getPhysical() {
        return Optional.ofNullable(physical);
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
