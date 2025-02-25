package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class MythicModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalMythicKey;

    public MythicModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalMythicKey = new NamespacedKey(plugin, "original-mythic-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalMythicId(item, getRealMythicId(item));
        }
    }

    /**
     * Get the original Mythic ID of the item.
     *
     * @param item The item
     * @return The original mythic ID
     */
    public String getOriginalMythicId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalMythicKey, PersistentDataType.STRING);
    }

    private void setOriginalMythicId(ItemStack item, String mythicId) {
        var meta = item.getItemMeta();
        if (mythicId != null) {
            meta.getPersistentDataContainer().set(originalMythicKey, PersistentDataType.STRING, mythicId);
        } else {
            meta.getPersistentDataContainer().remove(originalMythicKey);
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
    public String getRealMythicId(ItemStack item) {
        String mythicId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            mythicId = getOriginalMythicId(item);
        } else if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            var id = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
            if (id != null) {
                mythicId = id;
            }
        }
        return mythicId;
    }

}
