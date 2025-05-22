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

    private final HMCWraps plugin;

    public LoreModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalLoreKey = new NamespacedKey(plugin, "original-lore");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var originalLore = getOriginalLore(item);
        var currentLore = meta.getLore();
        if (currentWrap != null) {
            meta.setLore(originalLore);
        }
        if (wrap != null && wrap.getWrapLore() != null) {
            var lore = wrap.getWrapLore().stream().map(entry -> StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, entry))).toList();
            meta.setLore(lore);
        }
        if (wrap == null) {
            meta.getPersistentDataContainer().remove(originalLoreKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalLore(item, currentLore);
        }
    }

    private void setOriginalLore(ItemStack item, List<String> lore) {
        var meta = item.getItemMeta();
        if (lore != null) {
            meta.getPersistentDataContainer().set(originalLoreKey, PersistentDataType.STRING,
                    lore.stream().map(entry -> entry.replace("§", "&")).collect(Collectors.joining(SEPARATOR)));
        } else {
            meta.getPersistentDataContainer().remove(originalLoreKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original lore of the item.
     *
     * @param item The item
     * @return The original lore
     */
    public List<String> getOriginalLore(ItemStack item) {
        var meta = item.getItemMeta();
        var lore = new ArrayList<String>();
        var loreSettings = plugin.getConfiguration().getPreservation().getLore();
        if (loreSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalLoreKey, PersistentDataType.STRING);
            if (data != null) {
                Arrays.stream(data.split(SEPARATOR)).map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
            }
            return lore;
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

}
