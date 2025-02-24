package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import com.nexomc.nexo.api.NexoItems;
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

public class NexoModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalNexoKey;

    public NexoModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalNexoKey = new NamespacedKey(plugin, "original-nexo-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalNexoId(item, getRealNexoId(item));
        }
    }

    /**
     * Get the original Nexo ID of the item.
     *
     * @param item The item
     * @return The original Nexo ID
     */
    public String getOriginalNexoId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalNexoKey, PersistentDataType.STRING);
    }

    private void setOriginalNexoId(ItemStack item, String nexoId) {
        if (nexoId != null) {
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(originalNexoKey, PersistentDataType.STRING, nexoId);
            item.setItemMeta(meta);
        }
    }

    /**
     * Get the real Nexo ID of the item. If the item is wrapped, the original color will be returned.
     * If it isn't wrapped, the current color will be returned.
     *
     * @param item The item
     * @return The real Nexo ID
     */
    public String getRealNexoId(ItemStack item) {
        String nexoId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            nexoId = getOriginalNexoId(item);
        } else if (Bukkit.getPluginManager().getPlugin("Nexo") != null) {
            var id = NexoItems.idFromItem(item);
            if (id != null) {
                nexoId = id;
            }
        }
        return nexoId;
    }

}
