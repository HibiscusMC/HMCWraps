package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.VersionUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ItemModelModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalItemModelKey;

    public ItemModelModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalItemModelKey = new NamespacedKey(plugin, "original-item-model");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var originalItemModel = getOriginalItemModel(item);
        NamespacedKey currentItemModel = null;
        if (VersionUtil.itemModelSupported() && item.getItemMeta().hasItemModel()) {
            currentItemModel = item.getItemMeta().getItemModel();
        }
        if (VersionUtil.itemModelSupported()) {
            var meta = item.getItemMeta();
            if (wrap != null) {
                if (wrap.getItemModel() != null) {
                    meta.setItemModel(wrap.getItemModel());
                }
            } else {
                meta.setItemModel(originalItemModel);
            }
            item.setItemMeta(meta);
        }
        if (wrap != null && currentWrap == null) {
            setOriginalItemModel(item, currentItemModel);
        }
    }

    /**
     * Get the original item model of the item.
     *
     * @param item The item
     * @return The original item model
     */
    public NamespacedKey getOriginalItemModel(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(originalItemModelKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return NamespacedKey.fromString(data);
    }

    private void setOriginalItemModel(ItemStack item, NamespacedKey itemModel) {
        var meta = item.getItemMeta();
        if (itemModel != null) {
            meta.getPersistentDataContainer().set(originalItemModelKey, PersistentDataType.STRING, itemModel.toString());
        } else {
            meta.getPersistentDataContainer().remove(originalItemModelKey);
        }
        item.setItemMeta(meta);
    }

}
