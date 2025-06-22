package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalItemsAdderKey;

    public ItemsAdderModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalItemsAdderKey = new NamespacedKey(plugin, "original-itemsadder-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var originalItemsAdderId = getOriginalItemsAdderId(item);
        if (currentWrap != null) {
            setOriginalItemsAdderId(item, getRealItemsAdderId(item));
        }
        if (wrap != null) {
            if (wrap.getId() != null && wrap.getId().startsWith("itemsadder:")) {
                setItemsAdderNBT(item, wrap.getId().substring(11));
            }
        } else {
            if (originalItemsAdderId != null || (currentWrap != null && currentWrap.getId() != null && currentWrap.getId().startsWith("itemsadder:"))) {
                setItemsAdderNBT(item, originalItemsAdderId);
            }
        }
    }

    private void setItemsAdderNBT(ItemStack item, String id) {
        NBT.modify(item, nbt -> {
            var split = id != null ? id.split(":") : new String[0];
            var iaCompound = nbt.getCompound("itemsadder");
            if (iaCompound != null) {
                iaCompound.removeKey("namespace");
                iaCompound.removeKey("id");
                if (iaCompound.getKeys().isEmpty()) {
                    nbt.removeKey("itemsadder");
                }
            }
            if (split.length == 2) {
                iaCompound = nbt.getOrCreateCompound("itemsadder");
                iaCompound.setString("namespace", split[0]);
                iaCompound.setString("id", split[1]);
            }
        });
    }

    /**
     * Get the original ItemsAdder ID of the item.
     *
     * @param item The item
     * @return The original ItemsAdder ID
     */
    public String getOriginalItemsAdderId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalItemsAdderKey, PersistentDataType.STRING);
    }

    private void setOriginalItemsAdderId(ItemStack item, String id) {
        var meta = item.getItemMeta();
        if (id != null) {
            meta.getPersistentDataContainer().set(originalItemsAdderKey, PersistentDataType.STRING, id);
        } else {
            meta.getPersistentDataContainer().remove(originalItemsAdderKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real ItemsAdder ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     * @param item The item
     * @return The real ItemsAdder ID
     */
    public String getRealItemsAdderId(ItemStack item) {
        String itemsAdderId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            itemsAdderId = getOriginalItemsAdderId(item);
        } else if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            var id = CustomStack.byItemStack(item);
            if (id != null) {
                itemsAdderId = id.getNamespacedID();
            }
        }
        return itemsAdderId;
    }

}
