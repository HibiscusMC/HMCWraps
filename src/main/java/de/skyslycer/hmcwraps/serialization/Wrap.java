package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.IHMCWraps;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConfigSerializable
public class Wrap extends SerializableItem implements IWrap {

    private Boolean preview = true;
    private String uuid;
    private @Nullable PhysicalWrap physical;
    private @Nullable String permission;
    private @Nullable String lockedName;
    private @Nullable List<String> lockedLore;
    private @Nullable SerializableItem lockedItem;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public Wrap(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags,
                @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments, @Nullable Integer amount, Boolean preview,
                String uuid, @Nullable String color, @Nullable PhysicalWrap physical, @Nullable String permission, @Nullable String lockedName,
                @Nullable List<String> lockedLore, @Nullable SerializableItem lockedItem, @Nullable HashMap<String, HashMap<String, List<String>>> actions) {
        super(id, name, glow, lore, flags, modelId, enchantments, amount, color);
        this.preview = preview;
        this.uuid = uuid;
        this.physical = physical;
        this.permission = permission;
        this.lockedName = lockedName;
        this.lockedLore = lockedLore;
        this.lockedItem = lockedItem;
        this.actions = actions;
    }

    public Wrap() {
    }

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
    public SerializableItem getLockedItem() {
        return lockedItem;
    }

    @Override
    public ItemStack toPermissionItem(IHMCWraps plugin, Player player) {
        if (!plugin.getConfiguration().getPermissionSettings().isPermissionVirtual() || hasPermission(player)) {
            return super.toItem(plugin, player);
        } else if (getLockedItem() == null) {
            var item = super.toItem(plugin, player);
            var builder = ItemBuilder.from(item);
            if (getLockedName() != null) {
                builder.name(player != null ? StringUtil.parseComponent(player, getLockedName()) : StringUtil.parseComponent(getLockedName()));
            }
            if (getLockedName() != null) {
                builder.lore(player == null ? getLockedLore().stream().map(StringUtil::parseComponent).toList()
                        : getLockedLore().stream().map(string -> StringUtil.parseComponent(player, string)).toList());
            }
            return builder.build();
        } else {
            return getLockedItem().toItem(plugin, player);
        }
    }

    public record WrapValues(int modelId, Color color) implements IWrapValues {

        public WrapValues(int modelId, @Nullable Color color) {
            this.modelId = modelId;
            this.color = color;
        }

    }

}
