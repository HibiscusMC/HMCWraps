package de.skyslycer.hmcwraps.serialization.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.inventory.InventoryItem;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.wrap.range.RangeSettings;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Wrap extends SerializableItem {

    private @Nullable Boolean preview;
    private String uuid;
    private @Nullable PhysicalWrap physical;
    private @Nullable String permission;
    private @Nullable String lockedName;
    private @Nullable List<String> lockedLore;
    private @Nullable InventoryItem lockedItem;
    private @Nullable InventoryItem equippedItem;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;
    private @Nullable HashMap<String, HashMap<String, List<String>>> inventoryActions;
    private @Nullable String wrapName;
    private @Nullable List<String> wrapLore;
    private @Nullable RangeSettings range;
    private @Nullable String wrapNbt;
    private @Nullable List<String> wrapFlags;
    private @Nullable String armorImitation;
    private @Nullable Integer wrapDurability;
    private @Nullable Integer sort;
    private @Nullable Boolean upsideDownPreview;

    public Wrap(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore,
                @Nullable Integer modelId, String uuid, @Nullable PhysicalWrap physical,
                @Nullable String permission, @Nullable InventoryItem lockedItem) {
        super(id, name, glow, lore, null, modelId, null, null, null, null, null, null, null, null, null);
        this.preview = true;
        this.uuid = uuid;
        this.physical = physical;
        this.permission = permission;
        this.lockedItem = lockedItem;
    }

    public Wrap(String id, String name, @Nullable List<String> lore,
                @Nullable Integer modelId, String uuid, @Nullable String color, Integer amount, @Nullable List<String> flags,
                @Nullable Map<String, Integer> enchantments) {
        super(id, name, null, lore, flags, modelId, enchantments, amount, color, null, null, null, null, null, null);
        this.uuid = uuid;
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
        return preview == null || preview;
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

    @Nullable
    public HashMap<String, HashMap<String, List<String>>> getInventoryActions() {
        return inventoryActions;
    }

    public boolean hasPermission(CommandSender sender) {
        if (getPermission() == null) {
            return true;
        }
        return sender.hasPermission(getPermission());
    }

    @Nullable
    public InventoryItem getLockedItem() {
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

    @Nullable
    public String getWrapNbt() {
        return wrapNbt;
    }

    @Nullable
    public List<String> getWrapFlags() {
        return wrapFlags;
    }

    @Nullable
    public String getArmorImitationType() {
        if (armorImitation == null) {
            return null;
        }
        if (armorImitation.equalsIgnoreCase("true")) {
            return "LEATHER";
        } else {
            return armorImitation.toUpperCase();
        }
    }

    @Nullable
    public Integer getWrapDurability() {
        return wrapDurability;
    }

    @Nullable
    public InventoryItem getEquippedItem() {
        return equippedItem;
    }

    @Nullable
    public Integer getSort() {
        return sort;
    }

    @Nullable
    public Boolean isUpsideDownPreview() {
        return upsideDownPreview;
    }

    public ItemStack toPermissionItem(HMCWraps plugin, Material type, Player player) {
        if (!plugin.getConfiguration().getPermissions().isPermissionVirtual() || hasPermission(player)) {
            return super.toItem(plugin, player, type);
        } else if (getLockedItem() == null) {
            var item = super.toItem(plugin, player, type);
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

    public record WrapValues(int modelId, Color color, String name, List<String> lore, List<ItemFlag> flags, String itemsAdder,
                             String oraxen, String mythic, String material, String trim, String trimMaterial, boolean hideTrim) {
    }

}
