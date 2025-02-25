package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.VersionUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

public class TrimModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalTrimKey;
    private final NamespacedKey originalTrimMaterialKey;
    private final NamespacedKey trimsUsedKey;

    public TrimModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalTrimKey = new NamespacedKey(plugin, "original-trim");
        this.originalTrimMaterialKey = new NamespacedKey(plugin, "original-trim-material");
        this.trimsUsedKey = new NamespacedKey(plugin, "trims-used");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var originalTrim = getOriginalTrim(item);
        var originalTrimMaterial = getOriginalTrimMaterial(item);
        String currentTrim = null;
        String currentTrimMaterial = null;
        if (VersionUtil.trimsSupported() && item.getItemMeta() instanceof ArmorMeta armorMeta && armorMeta.getTrim() != null) {
            currentTrim = armorMeta.getTrim().getPattern().getKey().toString();
            currentTrimMaterial = armorMeta.getTrim().getMaterial().getKey().toString();
        }
        var meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(trimsUsedKey);
        item.setItemMeta(meta);
        if (wrap != null) {
            if (VersionUtil.trimsSupported() && item.getItemMeta() instanceof ArmorMeta armorMeta) {
                if (wrap.isRemoveTrim() == Boolean.TRUE) {
                    armorMeta.setTrim(null);
                    item.setItemMeta(armorMeta);
                }
                if (wrap.getTrim() != null && wrap.getTrimMaterial() != null) {
                    try {
                        armorMeta.setTrim(new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(wrap.getTrimMaterial())),
                                Registry.TRIM_PATTERN.get(NamespacedKey.fromString(wrap.getTrim()))));
                        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
                       setTrimsUsed(item, true);
                        item.setItemMeta(armorMeta);
                    } catch (IllegalArgumentException exception) {
                        plugin.getLogger().warning("Failed to set trim for item " + wrap.getUuid() + " with trim " + wrap.getTrim()
                                + " and material " + wrap.getTrimMaterial() + "! It seems to not be a valid trim. Please check your configuration!");
                    }
                }
            }
        } else {
            if (VersionUtil.trimsSupported() && item.getItemMeta() instanceof ArmorMeta armorMeta) {
                try {
                    if (originalTrim == null || originalTrimMaterial == null) {
                        armorMeta.setTrim(null);
                    } else {
                        armorMeta.setTrim(new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(originalTrimMaterial)),
                                Registry.TRIM_PATTERN.get(NamespacedKey.fromString(originalTrim))));
                    }
                    item.setItemMeta(armorMeta);
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning("Failed to set trim for player " + player.getName() + " with trim " + originalTrim + " and material "
                            + originalTrimMaterial + "! It seems to not be a valid trim. This is being set while unwrapping to preserve the original trim, which has since been removed.");
                }
            }
        }
        if (wrap != null && currentWrap == null) {
            setOriginalTrim(item, currentTrim);
            setOriginalTrimMaterial(item, currentTrimMaterial);
        }
    }

    /**
     * Get the original trim of the item.
     *
     * @param item The item
     * @return The original trim
     */
    public String getOriginalTrim(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalTrimKey, PersistentDataType.STRING);
    }

    /**
     * Get the original trim material of the item.
     *
     * @param item The item
     * @return The original trim material
     */
    public String getOriginalTrimMaterial(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalTrimMaterialKey, PersistentDataType.STRING);
    }

    private void setOriginalTrim(ItemStack item, String trim) {
        var meta = item.getItemMeta();
        if (trim != null) {
            meta.getPersistentDataContainer().set(originalTrimKey, PersistentDataType.STRING, trim);
        } else {
            meta.getPersistentDataContainer().remove(originalTrimKey);
        }
        item.setItemMeta(meta);
    }

    private void setOriginalTrimMaterial(ItemStack item, String trimMaterial) {
        var meta = item.getItemMeta();
        if (trimMaterial != null) {
            meta.getPersistentDataContainer().set(originalTrimMaterialKey, PersistentDataType.STRING, trimMaterial);
        } else {
            meta.getPersistentDataContainer().remove(originalTrimMaterialKey);
        }
        item.setItemMeta(meta);
    }

    private void setTrimsUsed(ItemStack item, boolean used) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(trimsUsedKey, PersistentDataType.BOOLEAN, used);
        editing.setItemMeta(meta);
    }

    /**
     * Check if trims are used on an item.
     *
     * @param item The item
     * @return If trims are used
     */
    public boolean isTrimsUsed(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(trimsUsedKey, PersistentDataType.BOOLEAN);
    }

}
