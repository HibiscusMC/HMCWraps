package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.util.StringUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Wrap extends SerializableItem implements IWrap {

    private Boolean preview = true;
    private String uuid;
    private @Nullable String color;
    private @Nullable PhysicalWrap physical;
    private @Nullable String permission;
    private @Nullable String lockedName;
    private @Nullable List<String> lockedLore;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public Wrap(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags, @Nullable Integer modelId,
            @Nullable Map<String, Integer> enchantments, @Nullable Integer amount, Boolean preview, String uuid, @Nullable String color,
            @Nullable PhysicalWrap physical, @Nullable String permission, @Nullable String lockedName, @Nullable List<String> lockedLore,
            @Nullable HashMap<String, HashMap<String, List<String>>> actions) {
        super(id, name, glow, lore, flags, modelId, enchantments, amount);
        this.preview = preview;
        this.uuid = uuid;
        this.color = color;
        this.physical = physical;
        this.permission = permission;
        this.lockedName = lockedName;
        this.lockedLore = lockedLore;
        this.actions = actions;
    }

    public Wrap() { }

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

    @Override
    @Nullable
    public String getLockedName() {
        return lockedName;
    }

    @Override
    @Nullable
    public List<String> getLockedLore() {
        return lockedLore;
    }

    @Override
    @Nullable
    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        if (getPermission().isEmpty()) {
            return true;
        }
        return sender.hasPermission(getPermission().get());
    }

    @Override
    @Nullable
    public Color getColor() {
        return StringUtil.colorFromString(color);
    }

    @Override
    @Nullable
    public String getName(Player player) {
        return !hasPermission(player) && getLockedName() != null ? getLockedName() : getName();
    }

    @Override
    @Nullable
    public List<String> getLore(Player player) {
        return !hasPermission(player) && getLockedLore() != null ? getLockedLore() : getLore();
    }

    public static class WrapValues implements IWrapValues {

        private final int modelId;
        private Color color;

        public WrapValues(int modelId, @Nullable Color color) {
            this.modelId = modelId;
            this.color = color;
        }

        @Override
        public int getModelId() {
            return modelId;
        }

        @Override
        public Color getColor() {
            return color;
        }

    }

}
