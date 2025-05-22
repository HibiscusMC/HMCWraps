package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;

public class ModelDataModifier implements WrapModifier {

    private final NamespacedKey originalModelIdKey;

    private final HMCWraps plugin;

    public ModelDataModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalModelIdKey = new NamespacedKey(plugin, "original-model-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var originalModleId = getOriginalModelId(item);
        Integer currentModelId = null;
        var meta = item.getItemMeta();
        if (meta.hasCustomModelData()) {
            currentModelId = meta.getCustomModelData();
        }
        meta.setCustomModelData(wrap == null ? originalModleId : wrap.getModelId());
        if (wrap == null) {
            meta.getPersistentDataContainer().remove(originalModelIdKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalModelId(item, currentModelId);
        }
    }

    private void setOriginalModelId(ItemStack item, Integer modelData) {
        var meta = item.getItemMeta();
        if (modelData != null) {
            meta.getPersistentDataContainer().set(originalModelIdKey, PersistentDataType.INTEGER, modelData);
        } else {
            meta.getPersistentDataContainer().remove(originalModelIdKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original model id of the item.
     *
     * @param item The item
     * @return The original model id
     */
    public Integer getOriginalModelId(ItemStack item) {
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

    /**
     * Get the real model id of the item. If the item is wrapped, the original model id will be returned.
     * If it isn't wrapped, the current model id will be returned.
     *
     * @param item The item
     * @return The real model id
     */
    public int getRealModelId(ItemStack item) {
        var modelData = -1;
        if (plugin.getWrapper().getWrap(item) != null) {
            modelData = getOriginalModelId(item);
        } else if (item.getItemMeta().hasCustomModelData()) {
            try { // Added to prevent error with racking datapack
                modelData = item.getItemMeta().getCustomModelData();
            } catch (Exception ignored) { }
        }
        return modelData;
    }

}
