package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

public class ColorModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalColorKey;

    public ColorModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalColorKey = new NamespacedKey(plugin, "original-color");
    }

    @Override
    public void wrap(Wrap wrap, Wrap currentWrap, ItemStack item, Player player) {
        Color currentColor = null;
        Color originalColor = getOriginalColor(item);
        if (wrap != null) {
            if (wrap.getColor() != null && item.getItemMeta() instanceof LeatherArmorMeta leatherMeta) {
                currentColor = leatherMeta.getColor();
                leatherMeta.setColor(wrap.getColor());
                if (wrap.getArmorImitationType() != null && wrap.getArmorImitationType().equalsIgnoreCase("LEATHER")) {
                    leatherMeta.addItemFlags(ItemFlag.HIDE_DYE);
                }
                item.setItemMeta(leatherMeta);
            }
        } else {
            if (item.getItemMeta() instanceof LeatherArmorMeta leatherMeta) {
                leatherMeta.setColor(originalColor);
                item.setItemMeta(leatherMeta);
            }
            var newMeta = item.getItemMeta();
            newMeta.getPersistentDataContainer().remove(originalColorKey);
            item.setItemMeta(newMeta);
        }
        if (wrap != null && currentWrap == null) {
            setOriginalColor(item, currentColor);
        }
    }

    /**
     * Get the original color of the item.
     *
     * @param item The item
     * @return The original color
     */
    public Color getOriginalColor(ItemStack item) {
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

    private void setOriginalColor(ItemStack item, Color color) {
        var meta = item.getItemMeta();
        if (color != null) {
            meta.getPersistentDataContainer().set(originalColorKey, PersistentDataType.INTEGER, color.asRGB());
        } else {
            meta.getPersistentDataContainer().remove(originalColorKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real color of the item. If the item is wrapped, the original color will be returned.
     * If it isn't wrapped, the current color will be returned.
     *
     * @param item The item
     * @return The real color
     */
    public Color getRealColor(ItemStack item) {
        Color color = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            color = getOriginalColor(item);
        } else if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
            color = meta.getColor();
        }
        return color;
    }

}
