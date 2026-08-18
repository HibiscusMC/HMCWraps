package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LoreModifier implements WrapModifier {

    private final NamespacedKey originalLoreKey;
    private final NamespacedKey addedLoreKey;

    private final HMCWraps plugin;

    public LoreModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalLoreKey = new NamespacedKey(plugin, "original-lore");
        this.addedLoreKey = new NamespacedKey(plugin, "added-lore");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var originalLore = getOriginalLore(item);
        var addedLore = getAddedLore(item);
        var currentLore = meta.getLore();
        if (currentWrap != null) {
            if (addedLore != null && !addedLore.isEmpty()) {
                var updatedLore = currentLore == null ? new ArrayList<String>() : new ArrayList<>(currentLore);
                updatedLore.removeIf(line -> addedLore.stream().anyMatch(orig -> orig.equalsIgnoreCase(line)));
                meta.setLore(updatedLore.isEmpty() ? null : updatedLore);
                meta.getPersistentDataContainer().remove(addedLoreKey);
            } else if (originalLore != null) {
                meta.setLore(originalLore.isEmpty() ? null : originalLore);
            }
            item.setItemMeta(meta);
            currentLore = meta.getLore();
        }
        if (wrap != null && wrap.getWrapLore() != null) {
            var type = wrap.getWrapLoreType();
            var wrapLore = wrap.getWrapLore().stream()
                    .map(entry -> StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, entry,
                            StringUtil.wrapPlaceholders(plugin, wrap, item))))
                    .toList();
            if (type == null) {
                meta.setLore(wrapLore.isEmpty() ? null : wrapLore);
            } else {
                var combinedLore = new ArrayList<String>();
                if (type == Type.PREPEND) {
                    combinedLore.addAll(wrapLore);
                    if (currentLore != null) {
                        combinedLore.addAll(currentLore);
                    }
                } else if (type == Type.APPEND) {
                    if (currentLore != null) {
                        combinedLore.addAll(currentLore);
                    }
                    combinedLore.addAll(wrapLore);
                }
                meta.setLore(combinedLore.isEmpty() ? null : combinedLore);
                item.setItemMeta(meta);
                setAddedLore(item, wrapLore);
                meta = item.getItemMeta();
            }
        }
        if (wrap == null) {
            meta.getPersistentDataContainer().remove(originalLoreKey);
            meta.getPersistentDataContainer().remove(addedLoreKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalLore(item, currentLore, wrap.getWrapLore() != null);
        }
    }

    private void setAddedLore(ItemStack item, List<String> lore) {
        var meta = item.getItemMeta();
        if (lore == null || lore.isEmpty()) {
            meta.getPersistentDataContainer().remove(addedLoreKey);
        } else {
            meta.getPersistentDataContainer().set(addedLoreKey, PersistentDataType.STRING,
                    lore.stream().map(entry -> entry.replace("§", "&")).collect(Collectors.joining(SEPARATOR)));
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the added lore of the item.
     * This set when the lore is changed by using the PREPEND or APPEND modifier.
     * Always check for this first before getting the original lore.
     *
     * @param item The item
     * @return The added lore
     */
    public List<String> getAddedLore(ItemStack item) {
        var lore = new ArrayList<String>();
        var data = item.getItemMeta().getPersistentDataContainer().get(addedLoreKey, PersistentDataType.STRING);
        if (data != null) {
            Arrays.stream(data.split(SEPARATOR)).map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
        }
        return lore;
    }

    private void setOriginalLore(ItemStack item, List<String> lore, boolean changed) {
        var meta = item.getItemMeta();
        if (changed) {
            if (lore == null) {
                meta.getPersistentDataContainer().set(originalLoreKey, PersistentDataType.STRING, "");
            } else {
                meta.getPersistentDataContainer().set(originalLoreKey, PersistentDataType.STRING,
                        lore.stream().map(entry -> entry.replace("§", "&")).collect(Collectors.joining(SEPARATOR)));
            }
        } else {
            meta.getPersistentDataContainer().remove(originalLoreKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original lore of the item.
     *
     * @param item The item
     * @return The original lore, null if it wasn't changed and an empty list if it was changed but originally had none
     */
    public List<String> getOriginalLore(ItemStack item) {
        var meta = item.getItemMeta();
        var lore = new ArrayList<String>();
        var loreSettings = plugin.getConfiguration().getPreservation().getLore();
        if (loreSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalLoreKey, PersistentDataType.STRING);
            if (data != null) {
                Arrays.stream(data.split(SEPARATOR)).map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
                return lore;
            } else {
                return null;
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
            return lore;
        }
        return item.getItemMeta().getLore();
    }

    public enum Type {
        PREPEND,
        APPEND
    }

}
