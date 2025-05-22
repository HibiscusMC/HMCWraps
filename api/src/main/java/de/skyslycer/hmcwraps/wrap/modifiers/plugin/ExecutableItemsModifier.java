package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ExecutableItemsModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalEIKey;

    public ExecutableItemsModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalEIKey = new NamespacedKey(plugin, "original-ei-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalEIId(item, getRealEIId(item));
        }
    }

    /**
     * Get the original Mythic ID of the item.
     *
     * @param item The item
     * @return The original mythic ID
     */
    public String getOriginalEIId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalEIKey, PersistentDataType.STRING);
    }

    private void setOriginalEIId(ItemStack item, String mythicId) {
        var meta = item.getItemMeta();
        if (mythicId != null) {
            meta.getPersistentDataContainer().set(originalEIKey, PersistentDataType.STRING, mythicId);
        } else {
            meta.getPersistentDataContainer().remove(originalEIKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real Mythic ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     *
     * @param item The item
     * @return The real mythic ID
     */
    public String getRealEIId(ItemStack item) {
        String eiId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            eiId = getOriginalEIId(item);
        } else if (Bukkit.getPluginManager().getPlugin("ExecutableItems") != null) {
            var eiItem = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(item);
            if (eiItem.isPresent()) {
                eiId = eiItem.get().getId();
            }
        }
        return eiId;
    }

}
