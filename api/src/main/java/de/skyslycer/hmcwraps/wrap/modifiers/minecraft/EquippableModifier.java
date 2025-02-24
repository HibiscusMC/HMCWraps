package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.VersionUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class EquippableModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalEquippableSlotKey;
    private final NamespacedKey originalEquippableModelKey;

    public EquippableModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalEquippableSlotKey = new NamespacedKey(plugin, "original-equippable-slot");
        this.originalEquippableModelKey = new NamespacedKey(plugin, "original-equippable-model");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var originalEquippableModel = getOriginalEquippableModel(item);
        var originalEquippableSlot = getOriginalEquippableSlot(item);
        NamespacedKey currentEquippableModel = null;
        EquipmentSlot currentEquippableSlot = null;
        var meta = item.getItemMeta();
        if (VersionUtil.equippableSupported() && meta.hasEquippable()) {
            currentEquippableModel = meta.getEquippable().getModel();
            currentEquippableSlot = meta.getEquippable().getSlot();
        }
        if (wrap != null) {
            if (VersionUtil.equippableSupported() && ((wrap.getEquippableSlot() != null && wrap.getEquippableModel() != null)
                    || (wrap.getEquippableSlot() != null && wrap.getEquippableSlot() == EquipmentSlot.HEAD))) {
                var newMeta = item.getItemMeta();
                var equippable = newMeta.getEquippable();
                equippable.setSlot(wrap.getEquippableSlot());
                if (wrap.getEquippableModel() != null) {
                    equippable.setModel(wrap.getEquippableModel());
                }
                newMeta.setEquippable(equippable);
                item.setItemMeta(newMeta);
            }
        } else {
            if (VersionUtil.equippableSupported()) {
                var newMeta = item.getItemMeta();
                if ((originalEquippableSlot != null && originalEquippableModel != null) || (originalEquippableSlot == EquipmentSlot.HEAD)) {
                    var equippable = newMeta.getEquippable();
                    equippable.setSlot(originalEquippableSlot);
                    if (originalEquippableModel != null) {
                        equippable.setModel(originalEquippableModel);
                    }
                    newMeta.setEquippable(equippable);
                } else {
                    newMeta.setEquippable(null);
                }
                item.setItemMeta(newMeta);
            }
        }
        if (wrap != null && currentWrap == null) {
            setOriginalEquippableSlot(item, currentEquippableSlot);
            setOriginalEquippableModel(item, currentEquippableModel);
        }
    }

    /**
     * Get the original equippable slot of the item.
     *
     * @param item The item
     * @return The original equippable slot
     */
    public EquipmentSlot getOriginalEquippableSlot(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(originalEquippableSlotKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return EquipmentSlot.valueOf(data);
    }

    /**
     * Get the original equippable model of the item.
     *
     * @param item The item
     * @return The original equippable model
     */
    public NamespacedKey getOriginalEquippableModel(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(originalEquippableModelKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return NamespacedKey.fromString(data);
    }

    private void setOriginalEquippableSlot(ItemStack item, EquipmentSlot slot) {
        if (slot != null) {
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(originalEquippableSlotKey, PersistentDataType.STRING, slot.name());
            item.setItemMeta(meta);
        }
    }

    private void setOriginalEquippableModel(ItemStack item, NamespacedKey model) {
        if (model != null) {
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(originalEquippableModelKey, PersistentDataType.STRING, model.toString());
            item.setItemMeta(meta);
        }
    }

}
