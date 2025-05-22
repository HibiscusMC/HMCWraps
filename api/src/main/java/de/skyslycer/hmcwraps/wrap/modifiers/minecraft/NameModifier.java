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

public class NameModifier implements WrapModifier {

    private final NamespacedKey originalNameKey;

    private final HMCWraps plugin;

    public NameModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalNameKey = new NamespacedKey(plugin, "original-name");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var originalName = getOriginalName(item);
        var currentName = meta.getDisplayName();
        if (currentWrap != null && (currentWrap.getWrapName() != null && (!Boolean.TRUE.equals(currentWrap.isApplyNameOnlyEmpty()) ||
                StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, currentWrap.getWrapName())).equals(meta.getDisplayName())))) {
            meta.setDisplayName(originalName);
        }
        if (wrap != null) {
            var originalActualName = currentWrap == null ? currentName : originalName;
            if (wrap.getWrapName() != null && (!Boolean.TRUE.equals(wrap.isApplyNameOnlyEmpty()) || originalActualName == null || originalActualName.isBlank())) {
                meta.setDisplayName(StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, wrap.getWrapName())).replace("%originalname%", originalActualName == null ? "" : originalActualName));
            }
        } else {
            meta.getPersistentDataContainer().remove(originalNameKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalName(item, currentName);
        }
    }

    private void setOriginalName(ItemStack item, String name) {
        var meta = item.getItemMeta();
        if (name != null) {
            meta.getPersistentDataContainer().set(originalNameKey, PersistentDataType.STRING, name.replace("§", "&"));
        } else {
            meta.getPersistentDataContainer().remove(originalNameKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original name of the item.
     *
     * @param item The item
     * @return The original name
     */
    public String getOriginalName(ItemStack item) {
        var meta = item.getItemMeta();
        String name = null;
        var nameSettings = plugin.getConfiguration().getPreservation().getName();
        if (nameSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalNameKey, PersistentDataType.STRING);
            if (data != null) {
                name = ChatColor.translateAlternateColorCodes('&', data);
            }
            return name;
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
            return name;
        }
        return item.getItemMeta().getDisplayName();
    }

}
