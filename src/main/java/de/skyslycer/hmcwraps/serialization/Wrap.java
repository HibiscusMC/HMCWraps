package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap extends SerializableItem implements IWrap {

    private Boolean preview = true;
    private String uuid;
    private @Nullable PhysicalWrap physical;
    private @Nullable String permission;
    private @Nullable String lockedName;
    private @Nullable List<String> lockedLore;

    @Override
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
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

    @Nullable
    public String getLockedName() {
        return lockedName;
    }

    @Nullable
    public List<String> getLockedLore() {
        return lockedLore;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (getPermission().isEmpty()) {
            return true;
        }
        return sender.hasPermission(getPermission().get());
    }

    @Override
    public String getName(Player player) {
        return !hasPermission(player) && getLockedName() != null ? getLockedName() : getName();
    }

    @Override
    public List<String> getLore(Player player) {
        return !hasPermission(player) && getLockedLore() != null ? getLockedLore() : getLore();
    }

}
