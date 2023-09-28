package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.events.ItemUnwrapEvent;
import de.skyslycer.hmcwraps.events.ItemWrapEvent;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap.WrapValues;
import de.skyslycer.hmcwraps.serialization.wrap.range.ValueRangeSettings;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.WrapNBTUtil;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WrapperImpl implements Wrapper {

    private static final String SEPARATOR = ";!;";

    private final HMCWrapsPlugin plugin;

    private final NamespacedKey physicalKey;
    private final NamespacedKey wrapIdKey;
    private final NamespacedKey playerKey;
    private final NamespacedKey physicalUnwrapperKey;
    private final NamespacedKey physicalWrapperKey;
    private final NamespacedKey originalModelIdKey;
    private final NamespacedKey originalColorKey;
    private final NamespacedKey originalNameKey;
    private final NamespacedKey originalLoreKey;
    private final NamespacedKey originalFlagsKey;
    private final NamespacedKey originalItemsAdderKey;
    private final NamespacedKey originalOraxenKey;
    private final NamespacedKey originalMythicKey;
    private final NamespacedKey originalMaterialKey;

    public WrapperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
        physicalKey = new NamespacedKey(plugin, "wrap-physical");
        wrapIdKey = new NamespacedKey(plugin, "wrap-id");
        playerKey = new NamespacedKey(plugin, "wrap-player");
        physicalUnwrapperKey = new NamespacedKey(plugin, "unwrapper");
        physicalWrapperKey = new NamespacedKey(plugin, "wrapper");
        originalModelIdKey = new NamespacedKey(plugin, "original-model-id");
        originalColorKey = new NamespacedKey(plugin, "original-color");
        originalNameKey = new NamespacedKey(plugin, "original-name");
        originalLoreKey = new NamespacedKey(plugin, "original-lore");
        originalFlagsKey = new NamespacedKey(plugin, "original-flags");
        originalItemsAdderKey = new NamespacedKey(plugin, "original-itemsadder-id");
        originalOraxenKey = new NamespacedKey(plugin, "original-oraxen-id");
        originalMythicKey = new NamespacedKey(plugin, "original-mythic-id");
        originalMaterialKey = new NamespacedKey(plugin, "original-material");
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
    public ItemStack setWrap(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player, boolean giveBack) {
        var event = new ItemWrapEvent(wrap, item, physical, player, giveBack);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return item;
        }
        return setWrapPrivate(event.getWrap(), event.getItem(), event.isPhysical(), event.getPlayer(), event.isGiveBack());
    }

    private ItemStack setWrapPrivate(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player, boolean giveBack) {
        if (item == null || item.getType().isAir()) {
            return item;
        }
        var editing = item.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical() != null && currentWrap.getPhysical().isKeepAfterUnwrap()
                && giveBack) {
            PlayerUtil.give(player, setPhysicalWrapper(currentWrap.getPhysical().toItem(plugin, player), currentWrap));
        }
        var originalData = getOriginalData(item);
        var meta = editing.getItemMeta();
        var originalName = meta.getDisplayName();
        var originalModelId = -1;
        var originalLore = meta.getLore();
        var originalFlags = meta.getItemFlags().stream().toList();
        var originalMaterial = "";
        Color originalColor = null;
        if (meta.hasCustomModelData()) {
            originalModelId = meta.getCustomModelData();
        }
        meta.getPersistentDataContainer().set(wrapIdKey, PersistentDataType.STRING, wrap == null ? "-" : wrap.getUuid());
        meta.setCustomModelData(wrap == null ? originalData.modelId() : wrap.getModelId());
        if (wrap != null) {
            if (wrap.getWrapName() != null) {
                meta.setDisplayName(StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, wrap.getWrapName())));
            }
            if (wrap.getWrapLore() != null) {
                var lore = wrap.getWrapLore().stream().map(entry -> StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, entry))).toList();
                meta.setLore(lore);
            }
            if (wrap.getWrapFlags() != null) {
                for (String flag : wrap.getWrapFlags()) {
                    try {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            if (wrap.getColor() != null && meta instanceof LeatherArmorMeta leatherMeta) {
                originalColor = leatherMeta.getColor();
                leatherMeta.setColor(wrap.getColor());
                editing.setItemMeta(leatherMeta);
            } else {
                editing.setItemMeta(meta);
            }
            if (wrap.getWrapNbt() != null) {
                editing = WrapNBTUtil.wrap(editing, wrap.getWrapNbt());
            }
            if (wrap.isArmorImitationEnabled()) {
                originalMaterial = editing.getType().toString();
                // TODO do stuff
            }
        } else {
            meta.setDisplayName(originalData.name());
            meta.setCustomModelData(originalData.modelId());
            meta.setLore(originalData.lore());
            meta.removeItemFlags(meta.getItemFlags().toArray(ItemFlag[]::new));
            meta.addItemFlags(originalData.flags().toArray(ItemFlag[]::new));
            if (originalData.material() != null && !originalData.material().isEmpty()) {
                // TODO set type back
            }
            if (meta instanceof LeatherArmorMeta leatherMeta) {
                leatherMeta.setColor(originalData.color());
                editing.setItemMeta(leatherMeta);
            } else {
                editing.setItemMeta(meta);
            }
            editing = WrapNBTUtil.unwrap(editing);
        }
        editing = setPhysical(editing.clone(), physical);
        if (wrap == null || currentWrap != null) {
            return editing;
        }
        String itemsAdderId = null;
        if (getWrap(item) == null && Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            var id = CustomStack.byItemStack(item);
            if (id != null) {
                itemsAdderId = id.getNamespacedID();
            }
        } else {
            itemsAdderId = getOriginalItemsAdderId(item);
        }
        String oraxenId = null;
        if (getWrap(item) == null && Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            var id = OraxenItems.getIdByItem(item);
            if (id != null) {
                oraxenId = id;
            }
        } else {
            oraxenId = getOriginalOraxenId(item);
        }
        String mythicId = null;
        if (getWrap(item) == null && Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            var id = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
            if (id != null) {
                itemsAdderId = id;
            }
        } else {
            mythicId = getOriginalMythicId(item);
        }
        return setOriginalData(editing, new WrapValues(originalModelId, originalColor, originalName, originalLore,
                originalFlags, itemsAdderId, oraxenId, mythicId, originalMaterial));
    }

    @Override
    public ItemStack removeWrap(ItemStack target, Player player, boolean giveBack) {
        var event = new ItemUnwrapEvent(target, player, getWrap(target), giveBack);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return target;
        }
        return removeWrapPrivate(event.getItem(), event.getPlayer(), event.isGiveBack());
    }

    private ItemStack removeWrapPrivate(ItemStack item, Player player, boolean giveBack) {
        var currentWrap = getWrap(item);
        if (currentWrap == null) {
            return item;
        }
        return setWrapPrivate(null, item, false, player, giveBack);
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
    public WrapValues getOriginalData(ItemStack item) {
        return new WrapValues(getOriginalModelId(item), getOriginalColor(item), getOriginalName(item),
                getOriginalLore(item), getOriginalFlags(item), getOriginalItemsAdderId(item), getOriginalOraxenId(item),
                getOriginalMythicId(item), getOriginalMaterial(item));
    }

    private int getOriginalModelId(ItemStack item) {
        var meta = item.getItemMeta();
        var modelData = -1;
        var modelDataSettings = plugin.getConfiguration().getPreservation().getModelId();
        if (modelDataSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalModelIdKey, PersistentDataType.INTEGER);
            if (data != null) {
                modelData = data;
            }
        } else if (modelDataSettings.isDefaultEnabled()) {
            var map = modelDataSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                modelData = map.get(item.getType().toString());
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    modelData = map.get(key);
                }
            }
        }
        return modelData;
    }

    private String getOriginalName(ItemStack item) {
        var meta = item.getItemMeta();
        String name = null;
        var nameSettings = plugin.getConfiguration().getPreservation().getName();
        if (nameSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalNameKey, PersistentDataType.STRING);
            if (data != null) {
                name = ChatColor.translateAlternateColorCodes('&', data);
            }
        } else if (nameSettings.isDefaultEnabled()) {
            var map = nameSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                name = StringUtil.LEGACY_SERIALIZER_AMPERSAND.serialize(StringUtil.parseComponent(map.get(item.getType().toString())));
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    name = StringUtil.LEGACY_SERIALIZER_AMPERSAND.serialize(StringUtil.parseComponent(map.get(key)));
                }
            }
        }
        return name;
    }

    private List<String> getOriginalLore(ItemStack item) {
        var meta = item.getItemMeta();
        var lore = new ArrayList<String>();
        var loreSettings = plugin.getConfiguration().getPreservation().getLore();
        if (loreSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalLoreKey, PersistentDataType.STRING);
            if (data != null) {
                Arrays.stream(data.split(SEPARATOR)).map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
            }
        } else if (loreSettings.isDefaultEnabled()) {
            var map = loreSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                map.get(item.getType().toString()).stream().map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    map.get(key).stream().map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
                }
            }
        }
        return lore;
    }

    private Color getOriginalColor(ItemStack item) {
        var colorSettings = plugin.getConfiguration().getPreservation().getColor();
        Color color = null;
        var meta = item.getItemMeta();
        if (colorSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalColorKey, PersistentDataType.INTEGER);
            if (data != null) {
                color = Color.fromRGB(data);
            }
        } else if (colorSettings.isDefaultEnabled()) {
            var map = colorSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                color = StringUtil.colorFromString(map.get(item.getType().toString()));
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    color = StringUtil.colorFromString(map.get(key));
                }
            }
        }
        return color;
    }

    private List<ItemFlag> getOriginalFlags(ItemStack item) {
        var settings = plugin.getConfiguration().getPreservation().getFlags();
        var list = new ArrayList<ItemFlag>();
        var meta = item.getItemMeta();
        if (settings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalFlagsKey, PersistentDataType.STRING);
            if (data != null) {
                var flags = data.split(SEPARATOR);
                for (String flag : flags) {
                    try {
                        list.add(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
        } else if (settings.isDefaultEnabled()) {
            var map = settings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                for (String flag : map.get(item.getType().toString())) {
                    try {
                        list.add(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    for (String flag : map.get(key)) {
                        try {
                            list.add(ItemFlag.valueOf(flag));
                        } catch (IllegalArgumentException ignored) { }
                    }
                }
            }
        }
        return list;
    }

    private String getOriginalItemsAdderId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalItemsAdderKey, PersistentDataType.STRING);
    }

    private String getOriginalOraxenId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalOraxenKey, PersistentDataType.STRING);
    }

    private String getOriginalMythicId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalMythicKey, PersistentDataType.STRING);
    }

    private String getOriginalMaterial(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalMaterialKey, PersistentDataType.STRING);
    }

    @Override
    public ItemStack setOriginalData(ItemStack item, WrapValues wrapValues) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(originalModelIdKey, PersistentDataType.INTEGER, wrapValues.modelId());
        if (wrapValues.color() != null) {
            meta.getPersistentDataContainer().set(originalColorKey, PersistentDataType.INTEGER, wrapValues.color().asRGB());
        }
        if (wrapValues.name() != null) {
            meta.getPersistentDataContainer().set(originalNameKey, PersistentDataType.STRING, wrapValues.name().replace("§", "&"));
        }
        if (wrapValues.lore() != null) {
            meta.getPersistentDataContainer().set(originalLoreKey, PersistentDataType.STRING,
                    wrapValues.lore().stream().map(entry -> entry.replace("§", "&")).collect(Collectors.joining(SEPARATOR)));
        }
        if (wrapValues.flags() != null) {
            meta.getPersistentDataContainer().set(originalFlagsKey, PersistentDataType.STRING,
                    wrapValues.flags().stream().map(ItemFlag::toString).collect(Collectors.joining(SEPARATOR)));
        }
        if (wrapValues.itemsAdder() != null) {
            meta.getPersistentDataContainer().set(originalItemsAdderKey, PersistentDataType.STRING, wrapValues.itemsAdder());
        }
        if (wrapValues.oraxen() != null) {
            meta.getPersistentDataContainer().set(originalOraxenKey, PersistentDataType.STRING, wrapValues.oraxen());
        }
        if (wrapValues.mythic() != null) {
            meta.getPersistentDataContainer().set(originalMythicKey, PersistentDataType.STRING, wrapValues.mythic());
        }
        if (wrapValues.material() != null) {
            meta.getPersistentDataContainer().set(originalMaterialKey, PersistentDataType.STRING, wrapValues.material());
        }
        editing.setItemMeta(meta);
        return editing;
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
        meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, physical ? (byte) 1 : (byte) 0);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isValid(ItemStack item, Wrap wrap) {
        var modelData = -1;
        if (getWrap(item) != null) {
            modelData = getOriginalModelId(item);
        } else if (item.getItemMeta().hasCustomModelData()) {
            modelData = item.getItemMeta().getCustomModelData();
        }
        Color color = null;
        if (getWrap(item) != null) {
            color = getOriginalColor(item);
        } else if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
            color = meta.getColor();
        }
        String itemsAdderId = null;
        if (getWrap(item) != null) {
            itemsAdderId = getOriginalItemsAdderId(item);
        } else if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            var id = CustomStack.byItemStack(item);
            if (id != null) {
                itemsAdderId = id.getNamespacedID();
            }
        }
        String oraxenId = null;
        if (getWrap(item) != null) {
            oraxenId = getOriginalOraxenId(item);
        } else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            var id = OraxenItems.getIdByItem(item);
            if (id != null) {
                oraxenId = id;
            }
        }
        String mythicId = null;
        if (getWrap(item) != null) {
            mythicId = getOriginalMythicId(item);
        } else if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            var id = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
            if (id != null) {
                mythicId = id;
            }
        }
        return wrap.getRange() == null || (isValidType(wrap.getRange().getModelId(), modelData) && isValidColor(wrap.getRange().getColor(), color) &&
                isValidType(wrap.getRange().getItemsAdder(), itemsAdderId) && isValidType(wrap.getRange().getOraxen(), oraxenId) && isValidType(wrap.getRange().getMythic(), mythicId));
    }

    private <T> boolean isValidType(ValueRangeSettings<T> settings, T value) {
        return (settings.getExclude() == null || !settings.getExclude().contains(value)) && (settings.getInclude() == null || settings.getInclude().contains(value));
    }

    private boolean isValidColor(ValueRangeSettings<String> settings, Color value) {
        List<Color> exclude = null;
        List<Color> include = null;
        if (settings.getExclude() != null) {
            exclude = settings.getExclude().stream().map(StringUtil::colorFromString).collect(Collectors.toList());
        }
        if (settings.getInclude() != null) {
            include = settings.getInclude().stream().map(StringUtil::colorFromString).collect(Collectors.toList());
        }
        return (exclude == null || !exclude.contains(value)) && (include == null || include.contains(value));
    }

}
