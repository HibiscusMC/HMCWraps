package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class TooltipStyleModifier implements WrapModifier {

    private final NamespacedKey originalTooltipKey;
    private final NamespacedKey noneKey;

    private final HMCWraps plugin;

    public TooltipStyleModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalTooltipKey = new NamespacedKey(plugin, "original-tooltip");
        this.noneKey = new NamespacedKey(plugin, "none");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var original = getOriginalTooltip(item);
        var current = meta.getTooltipStyle();
        if (currentWrap != null && original != null) {
            if (!original.equals(noneKey)) {
                meta.setTooltipStyle(original);
            } else {
                meta.setTooltipStyle(null);
            }
        }
        if (wrap != null && wrap.getWrapTooltipStyle() != null) {
            NamespacedKey tooltip = NamespacedKey.fromString(wrap.getWrapTooltipStyle());
            meta.setTooltipStyle(tooltip);
        }
        if (wrap == null) {
            meta.getPersistentDataContainer().remove(originalTooltipKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalTooltip(item, current, wrap.getWrapTooltipStyle() != null);
        }
    }

    private void setOriginalTooltip(ItemStack item, NamespacedKey tooltip, boolean changed) {
        var meta = item.getItemMeta();
        if (changed) {
            meta.getPersistentDataContainer().set(originalTooltipKey, PersistentDataType.STRING, tooltip == null ? noneKey.toString() : tooltip.toString());
        } else {
            meta.getPersistentDataContainer().remove(originalTooltipKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original tooltip style of the item.
     *
     * @param item The item to get the tooltip style from.
     * @return The original tooltip style, null if it wasn't changed and the none key, if it was changed but originally had none.
     */
    public NamespacedKey getOriginalTooltip(ItemStack item) {
        var meta = item.getItemMeta();
        NamespacedKey tooltip = null;
        var tooltipSettings = plugin.getConfiguration().getPreservation().getTooltipStyle();
        if (tooltipSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalTooltipKey, PersistentDataType.STRING);
            if (data != null) {
                tooltip = NamespacedKey.fromString(data);
            }
            return tooltip;
        } else if (tooltipSettings.isDefaultEnabled()) {
            var map = tooltipSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                tooltip = NamespacedKey.fromString(map.get(item.getType().toString()));
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    tooltip = NamespacedKey.fromString(map.get(key));
                }
            }
            return tooltip;
        }
        return null;
    }

}
