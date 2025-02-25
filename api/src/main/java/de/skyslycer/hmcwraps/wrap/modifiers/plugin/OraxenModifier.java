package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class OraxenModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalOraxenKey;

    public OraxenModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalOraxenKey = new NamespacedKey(plugin, "original-oraxen-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalOraxenId(item, getRealOraxenId(item));
        }
    }

    /**
     * Get the original Oraxen ID of the item.
     *
     * @param item The item
     * @return The original Oraxen ID
     */
    public String getOriginalOraxenId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalOraxenKey, PersistentDataType.STRING);
    }

    private void setOriginalOraxenId(ItemStack item, String oraxenId) {
        var meta = item.getItemMeta();
        if (oraxenId != null) {
            meta.getPersistentDataContainer().set(originalOraxenKey, PersistentDataType.STRING, oraxenId);
        } else {
            meta.getPersistentDataContainer().remove(originalOraxenKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real Oraxen ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     * @param item The item
     * @return The real Oraxen ID
     */
    public String getRealOraxenId(ItemStack item) {
        String oraxenId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            oraxenId = getOriginalOraxenId(item);
        } else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            var id = OraxenItems.getIdByItem(item);
            if (id != null) {
                oraxenId = id;
            }
        }
        return oraxenId;
    }

}
