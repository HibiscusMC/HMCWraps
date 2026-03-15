package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.MaterialUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import de.skyslycer.hmcwraps.wrap.ArmorModifiers;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ArmorImitationModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey fakeDurabilityKey;
    private final NamespacedKey fakeMaxDurabilityKey;
    private final NamespacedKey originalNativeMaxDurabilityKey;
    private final NamespacedKey customAttributesKey;
    private final NamespacedKey originalMaterialKey;

    public ArmorImitationModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.fakeDurabilityKey = new NamespacedKey(plugin, "fake-durability");
        this.fakeMaxDurabilityKey = new NamespacedKey(plugin, "fake-max-durability");
        this.originalNativeMaxDurabilityKey = new NamespacedKey(plugin, "original-native-max-durability");
        this.customAttributesKey = new NamespacedKey(plugin, "custom-attributes");
        this.originalMaterialKey = new NamespacedKey(plugin, "original-material");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        String currentMaterial = null;
        var originalMaterial = getOriginalMaterial(item);
        if (wrap != null) {
            if (currentWrap != null && !originalMaterial.isBlank()) {
                switchFromAlternative(item, originalMaterial);
            }
            resetFakeDurability(item);
            if (MaterialUtil.getAlternative(wrap.getArmorImitationType(), item.getType()) != item.getType()) {
                var maxDurability = item.getType().getMaxDurability();
                var currentDurability = maxDurability - ((Damageable) item.getItemMeta()).getDamage();
                var temp = item.getType().toString();
                var attributeModifiers = item.getItemMeta().getAttributeModifiers();
                if (switchToAlternative(item, wrap.getArmorImitationType())) {
                    var newMeta = ((Damageable) item.getItemMeta());
                    if (wrap.getWrapDurability() == null || wrap.getWrapDurability() != -1) { // skip durability conversion
                        int newDurability = item.getType().getMaxDurability();
                        var modelDurability = ((double) currentDurability / maxDurability) * newDurability;
                        newMeta.setDamage(newDurability - (int) modelDurability);
                        if (VersionUtil.hasDataComponents()) {
                            setNativeDurability(item, maxDurability, maxDurability);
                            newMeta = (Damageable) item.getItemMeta();
                        } else {
                            newMeta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, currentDurability);
                            newMeta.getPersistentDataContainer().set(fakeMaxDurabilityKey, PersistentDataType.INTEGER, (int) maxDurability);
                        }
                    }
                    newMeta.getPersistentDataContainer().set(customAttributesKey, PersistentDataType.BOOLEAN, attributeModifiers != null);
                    item.setItemMeta(newMeta);
                    currentMaterial = temp;
                }
            }
            if (wrap.getWrapDurability() != null && wrap.getWrapDurability() > 0) {
                if (VersionUtil.hasDataComponents()) {
                    System.out.println(wrap.getWrapDurability());
                    setNativeDurability(item, wrap.getWrapDurability(), item.getType().getMaxDurability());
                } else {
                    var maxDurability = item.getType().getMaxDurability();
                    var currentDurability = maxDurability - ((Damageable) item.getItemMeta()).getDamage();
                    var modelDurability = ((double) currentDurability / maxDurability) * wrap.getWrapDurability();
                    var newMeta = ((Damageable) item.getItemMeta());
                    newMeta.setDamage(maxDurability - currentDurability);
                    newMeta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, (int) modelDurability);
                    newMeta.getPersistentDataContainer().set(fakeMaxDurabilityKey, PersistentDataType.INTEGER, wrap.getWrapDurability());
                    item.setItemMeta(newMeta);
                }
            }
        } else {
            if (!originalMaterial.isBlank()) {
                switchFromAlternative(item, originalMaterial);
            }
            resetFakeDurability(item);
            var newMeta = item.getItemMeta();
            newMeta.getPersistentDataContainer().remove(originalMaterialKey);
            newMeta.getPersistentDataContainer().remove(customAttributesKey);
            item.setItemMeta(newMeta);
        }
        if (wrap != null && currentWrap == null) {
            setOriginalMaterial(item, currentMaterial);
        }
    }

    private boolean switchToAlternative(ItemStack editing, String alternative) {
        var newMaterial = MaterialUtil.getAlternative(alternative, editing.getType());
        var hasCustomAttributes = editing.getItemMeta().getAttributeModifiers() != null;
        var armorModifiers = ArmorModifiers.getFromMaterial(editing.getType().toString());
        editing.setType(newMaterial);
        if (!hasCustomAttributes) {
            if (editing.getType().toString().contains("_HELMET")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.HEAD, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().helmet());
            } else if (editing.getType().toString().contains("_CHESTPLATE")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.CHEST, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().chestplate());
            } else if (editing.getType().toString().contains("_LEGGINGS")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.LEGS, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().leggings());
            } else if (editing.getType().toString().contains("_BOOTS")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.FEET, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().boots());
            } else {
                return false;
            }
        }
        return true;
    }

    private void switchFromAlternative(ItemStack editing, String material) {
        editing.setType(Material.valueOf(material));
        if (!isCustomAttributes(editing)) {
            ArmorModifiers.removeAttributes(editing);
        }
    }

    private boolean isCustomAttributes(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(customAttributesKey, PersistentDataType.BOOLEAN);
        if (data == null) {
            return false;
        }
        return data;
    }

    private void resetFakeDurability(ItemStack item) {
        var newMeta = (Damageable) item.getItemMeta();
        if (newMeta.getPersistentDataContainer().has(fakeDurabilityKey, PersistentDataType.INTEGER)) {
            var currentDurability = getFakeDurability(item);
            var oldMaxDurability = getFakeMaxDurability(item);
            var newMaxDurability = item.getType().getMaxDurability();
            var newDurability = ((double) currentDurability / oldMaxDurability) * newMaxDurability;
            newMeta.setDamage(item.getType().getMaxDurability() - (int) newDurability);
            newMeta.getPersistentDataContainer().remove(fakeDurabilityKey);
            newMeta.getPersistentDataContainer().remove(fakeMaxDurabilityKey);
            item.setItemMeta(newMeta);
        }
        if (VersionUtil.hasDataComponents()) {
            resetNativeDurability(item);
        }
    }

    private void setNativeDurability(ItemStack item, int wrapDurability, int typeDurability) {
        var newMeta = (Damageable) item.getItemMeta();
        var container = newMeta.getPersistentDataContainer();
        container.set(originalNativeMaxDurabilityKey, PersistentDataType.INTEGER, newMeta.hasMaxDamage() ? newMeta.getMaxDamage() : 0);
        var currentMaxDurability = newMeta.hasMaxDamage() ? newMeta.getMaxDamage() : typeDurability;
        var currentDurability = Math.max(0, currentMaxDurability - newMeta.getDamage());
        var modelDurability = ((double) currentDurability / currentMaxDurability) * wrapDurability;
        newMeta.setMaxDamage(wrapDurability);
        newMeta.setDamage((int) (wrapDurability - Math.round(modelDurability)));
        newMeta.getPersistentDataContainer().remove(fakeDurabilityKey);
        newMeta.getPersistentDataContainer().remove(fakeMaxDurabilityKey);
        item.setItemMeta(newMeta);
    }

    private void resetNativeDurability(ItemStack item) {
        var newMeta = (Damageable) item.getItemMeta();
        var container = newMeta.getPersistentDataContainer();
        var oldMaxDurability = container.get(originalNativeMaxDurabilityKey, PersistentDataType.INTEGER);
        if (oldMaxDurability == null) {
            return;
        }
        var currentMaxDurability = newMeta.hasMaxDamage() ? newMeta.getMaxDamage() : item.getType().getMaxDurability();
        var currentDurability = Math.max(0, currentMaxDurability - newMeta.getDamage());
        var newMaxDurability = oldMaxDurability > 0 ? oldMaxDurability : item.getType().getMaxDurability();
        var newDurability = ((double) currentDurability / currentMaxDurability) * newMaxDurability;
        if (oldMaxDurability > 0) {
            newMeta.setMaxDamage(oldMaxDurability);
        } else {
            newMeta.setMaxDamage(null);
        }
        newMeta.setDamage(newMaxDurability - (int) Math.round(newDurability));
        container.remove(originalNativeMaxDurabilityKey);
        item.setItemMeta(newMeta);
    }

    /**
     * Get the fake durability of the item stored in the PDC.
     *
     * @param item The item
     * @return The fake durability
     */
    public int getFakeDurability(ItemStack item) {
        return getValue(item, fakeDurabilityKey);
    }

    /**
     * Set the fake durability of the item in the PDC.
     *
     * @param item The item
     * @param durability The durability
     */
    public void setFakeDurability(ItemStack item, int durability) {
        var meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, durability);
        item.setItemMeta(meta);
    }

    /**
     * Get the fake max durability of the item stored in the PDC.
     *
     * @param item The item
     * @return The fake max durability
     */
    public int getFakeMaxDurability(ItemStack item) {
        return getValue(item, fakeMaxDurabilityKey);
    }

    private int getValue(ItemStack item, NamespacedKey key) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return -1;
        }
        var data = meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (data == null) {
            return -1;
        }
        return data;
    }

    /**
     * Get the original material.
     *
     * @param item The item
     * @return The original material
     */
    public String getOriginalMaterial(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var value = container.get(originalMaterialKey, PersistentDataType.STRING);
        return value == null ? "" : value;
    }

    private void setOriginalMaterial(ItemStack item, String material) {
        var meta = item.getItemMeta();
        if (material != null) {
            meta.getPersistentDataContainer().set(originalMaterialKey, PersistentDataType.STRING, material);
        } else {
            meta.getPersistentDataContainer().remove(originalMaterialKey);
        }
        item.setItemMeta(meta);
    }

}
