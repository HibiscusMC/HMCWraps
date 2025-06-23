package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.events.ItemUnwrapEvent;
import de.skyslycer.hmcwraps.events.ItemWrapEvent;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.range.ValueRangeSettings;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifiers;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class WrapperImpl implements Wrapper {

    private final HMCWrapsPlugin plugin;
    private final WrapModifiers modifiers;

    private final NamespacedKey physicalKey;
    private final NamespacedKey wrapIdKey;
    private final NamespacedKey playerKey;
    private final NamespacedKey physicalUnwrapperKey;
    private final NamespacedKey physicalWrapperKey;

    public WrapperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
        this.modifiers = new WrapModifiers(plugin);
        this.physicalKey = new NamespacedKey(plugin, "wrap-physical");
        this.wrapIdKey = new NamespacedKey(plugin, "wrap-id");
        this.playerKey = new NamespacedKey(plugin, "wrap-player");
        this.physicalUnwrapperKey = new NamespacedKey(plugin, "unwrapper");
        this.physicalWrapperKey = new NamespacedKey(plugin, "wrapper");
    }

    private ItemStack setWrapPrivate(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player) {
        if (item == null || item.getType().isAir()) {
            return item;
        }
        var editing = item.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical() != null && currentWrap.getPhysical().isKeepAfterUnwrap()) {
            PlayerUtil.give(player, setPhysicalWrapper(currentWrap.getPhysical().toItem(plugin, player), currentWrap));
        }
        var meta = editing.getItemMeta();
        if (wrap != null) {
            meta.getPersistentDataContainer().set(wrapIdKey, PersistentDataType.STRING, wrap.getUuid());
        } else {
            meta.getPersistentDataContainer().remove(wrapIdKey);
        }
        meta.getPersistentDataContainer().remove(playerKey);
        editing.setItemMeta(meta);

        getModifiers().modelData().wrap(wrap, currentWrap, editing, player);
        getModifiers().armorImitation().wrap(wrap, currentWrap, editing, player);
        getModifiers().name().wrap(wrap, currentWrap, editing, player);
        getModifiers().lore().wrap(wrap, currentWrap, editing, player);
        getModifiers().flags().wrap(wrap, currentWrap, editing, player);
        getModifiers().color().wrap(wrap, currentWrap, editing, player);
        getModifiers().trim().wrap(wrap, currentWrap, editing, player);
        getModifiers().equippable().wrap(wrap, currentWrap, editing, player);
        getModifiers().itemModel().wrap(wrap, currentWrap, editing, player);
        getModifiers().glintOverride().wrap(wrap, currentWrap, editing, player);
        getModifiers().tooltipStyle().wrap(wrap, currentWrap, editing, player);
        getModifiers().nbt().wrap(wrap, currentWrap, editing, player);
        getModifiers().itemsAdder().wrap(wrap, currentWrap, editing, player);
        getModifiers().oraxen().wrap(wrap, currentWrap, editing, player);
        getModifiers().mythic().wrap(wrap, currentWrap, editing, player);
        getModifiers().executableItems().wrap(wrap, currentWrap, editing, player);
        getModifiers().nexo().wrap(wrap, currentWrap, editing, player);

        return setPhysical(editing.clone(), physical);
    }

    @Override
    public Wrap getWrap(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(wrapIdKey, PersistentDataType.STRING);
        if (data == null || data.equals("-")) {
            return null;
        }
        return plugin.getWrapsLoader().getWraps().get(data);
    }

    @Override
    public ItemStack setWrap(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player) {
        var event = new ItemWrapEvent(wrap, item, physical, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return item;
        }
        return setWrapPrivate(event.getWrap(), event.getItem(), event.isPhysical(), event.getPlayer());
    }

    @Override
    public ItemStack removeWrap(ItemStack target, Player player) {
        var event = new ItemUnwrapEvent(target, player, getWrap(target));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return target;
        }
        return removeWrapPrivate(event.getItem(), event.getPlayer());
    }

    private ItemStack removeWrapPrivate(ItemStack item, Player player) {
        var currentWrap = getWrap(item);
        if (currentWrap == null) {
            return item;
        }
        return setWrapPrivate(null, item, false, player);
    }

    @Override
    public ItemStack setPhysicalUnwrapper(ItemStack item) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalUnwrapperKey, PersistentDataType.BYTE, (byte) 1);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isPhysicalUnwrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(physicalUnwrapperKey, PersistentDataType.BYTE);
    }

    @Override
    public ItemStack setPhysicalWrapper(ItemStack item, Wrap wrap) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalWrapperKey, PersistentDataType.STRING, wrap.getUuid());
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public String getPhysicalWrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(physicalWrapperKey, PersistentDataType.STRING);
    }

    @Override
    public UUID getOwningPlayer(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(playerKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return UUID.fromString(data);
    }

    @Override
    public ItemStack setOwningPlayer(ItemStack item, UUID uuid) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(playerKey, PersistentDataType.STRING, uuid.toString());
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isOwningPlayer(ItemStack item, Player player) {
        var uuid = getOwningPlayer(item);
        if (uuid == null) {
            return false;
        }
        return player.getUniqueId().equals(uuid);
    }

    @Override
    public boolean isPhysical(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(physicalKey, PersistentDataType.BYTE);
        if (data == null) {
            return false;
        }
        return data.intValue() > 0;
    }

    @Override
    public ItemStack setPhysical(ItemStack item, boolean physical) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        if (physical) {
            meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(physicalKey);
        }
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isValid(ItemStack item, Wrap wrap) {
        return wrap.getRange() == null || (isValidType(wrap.getRange().getModelId(), getModifiers().modelData().getRealModelId(item))
                && isValidColor(wrap.getRange().getColor(), getModifiers().color().getRealColor(item)) &&
                isValidType(wrap.getRange().getItemsAdder(), getModifiers().itemsAdder().getRealItemsAdderId(item))
                && isValidType(wrap.getRange().getOraxen(), getModifiers().oraxen().getRealOraxenId(item))
                && isValidType(wrap.getRange().getMythic(), getModifiers().mythic().getRealMythicId(item))
                && isValidType(wrap.getRange().getExecutableItems(), getModifiers().executableItems().getRealEIId(item))
                && isValidType(wrap.getRange().getNexo(), getModifiers().nexo().getRealNexoId(item)));
    }

    private <T> boolean isValidType(ValueRangeSettings<T> settings, T value) {
        if (settings == null) {
            return true;
        }
        return (settings.getExclude() == null || !settings.getExclude().contains(value)) && (settings.getInclude() == null || settings.getInclude().contains(value));
    }

    private boolean isValidColor(ValueRangeSettings<String> settings, Color value) {
        if (settings == null) {
            return true;
        }
        if (value.equals(StringUtil.colorFromString("#A06540"))) {
            value = null;
        }
        List<Color> exclude = null;
        List<Color> include = null;
        if (settings.getExclude() != null) {
            exclude = settings.getExclude().stream().map(StringUtil::colorFromString).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (settings.getInclude() != null) {
            include = settings.getInclude().stream().map(StringUtil::colorFromString).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if ((exclude != null && exclude.contains(value)) || (settings.getExclude() != null && settings.getExclude().contains("none") && value == null)) {
            return false;
        }
        if ((include != null && !include.contains(value)) && (settings.getInclude() != null && (!settings.getInclude().contains("none") || value != null))) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isGloballyDisabled(ItemStack item) {
        if (plugin.getConfiguration().getGlobalDisable().getModelId().contains(getModifiers().modelData().getRealModelId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getColor().stream().map(StringUtil::colorFromString).toList().contains(getModifiers().color().getRealColor(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getItemsAdderId().contains(getModifiers().itemsAdder().getRealItemsAdderId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getOraxenId().contains(getModifiers().oraxen().getRealOraxenId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getMythicId().contains(getModifiers().mythic().getRealMythicId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getNexoId().contains(getModifiers().nexo().getRealNexoId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getExecutableItemsId().contains(getModifiers().executableItems().getRealEIId(item))) {
            return true;
        }
        return false;
    }

    @Override
    public WrapModifiers getModifiers() {
        return modifiers;
    }

}
