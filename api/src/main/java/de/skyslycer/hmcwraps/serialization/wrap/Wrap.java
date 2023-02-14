package de.skyslycer.hmcwraps.serialization.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.wrap.range.RangeSettings;
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

@ConfigSerializable
public class Wrap extends SerializableItem {

    private Boolean preview = true;
    private String uuid;
    private @Nullable PhysicalWrap physical;
    private @Nullable String permission;
    private @Nullable String lockedName;
    private @Nullable List<String> lockedLore;
    private @Nullable SerializableItem lockedItem;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;
    private @Nullable String wrapName;
    private @Nullable List<String> wrapLore;
    private @Nullable RangeSettings range;

    public Wrap(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore,
                @Nullable Integer modelId, String uuid, @Nullable PhysicalWrap physical,
                @Nullable String permission, @Nullable SerializableItem lockedItem) {
        super(id, name, glow, lore, null, modelId, null, null, null);
        this.preview = true;
        this.uuid = uuid;
        this.physical = physical;
        this.permission = permission;
        this.lockedItem = lockedItem;
    }

    public Wrap() {
    }

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

    @Nullable
    public String getLockedName() {
        return lockedName;
    }

    @Nullable
    public List<String> getLockedLore() {
        return lockedLore;
    }

    @Nullable
    public HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null) {
            return true;
        }
        return sender.hasPermission(getPermission());
    }

    @Nullable
    public SerializableItem getLockedItem() {
        return lockedItem;
    }

    @Nullable
    public String getWrapName() {
        return wrapName;
    }

    @Nullable
    public List<String> getWrapLore() {
        return wrapLore;
    }

    @Nullable
    public RangeSettings getRange() {
        return range;
    }

    public ItemStack toPermissionItem(HMCWraps plugin, Player player) {
        if (!plugin.getConfiguration().getPermissions().isPermissionVirtual() || hasPermission(player)) {
            return super.toItem(plugin, player);
        } else if (getLockedItem() == null) {
            var item = super.toItem(plugin, player);
            var builder = ItemBuilder.from(item);
            if (getLockedName() != null) {
                builder.name(player != null ? StringUtil.parseComponent(player, getLockedName()) : StringUtil.parseComponent(getLockedName()));
            }
            if (getLockedLore() != null) {
                builder.lore(player == null ? getLockedLore().stream().map(StringUtil::parseComponent).toList()
                        : getLockedLore().stream().map(string -> StringUtil.parseComponent(player, string)).toList());
            }
            return builder.build();
        } else {
            return getLockedItem().toItem(plugin, player);
        }
    }

    public record WrapValues(int modelId, Color color, String name, List<String> lore) { }

}
