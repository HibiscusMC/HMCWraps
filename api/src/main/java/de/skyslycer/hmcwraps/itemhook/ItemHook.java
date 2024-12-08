package de.skyslycer.hmcwraps.itemhook;

import de.skyslycer.hmcwraps.util.VersionUtil;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

public abstract class ItemHook {

    /**
     * Get the hook prefix.
     *
     * @return The hook prefix
     */
    abstract String getPrefix();

    /**
     * Get an item stack based on the input.
     *
     * @param id The input
     * @return The item stack
     */
    @Nullable
    abstract ItemStack get(String id);

    /**
     * Get the model id corresponding to the input.
     *
     * @param id The input
     * @return The model id
     */
    public int getModelId(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta().hasCustomModelData()) {
            return stack.getItemMeta().getCustomModelData();
        }
        return -1;
    }

    /**
     * Get the color corresponding to the input.
     *
     * @param id The input
     * @return The color
     */
    @Nullable
    public Color getColor(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            return meta.getColor();
        }
        return null;
    }

    /**
     * Get the trim pattern corresponding to the input.
     *
     * @param id The input
     * @return The trim pattern
     */
    @Nullable
    public String getTrimPattern(String id) {
        var stack = get(id);
        if (VersionUtil.trimsSupported() && stack != null && stack.getItemMeta() instanceof ArmorMeta meta && meta.getTrim() != null) {
            return meta.getTrim().getPattern().getKey().toString();
        }
        return null;
    }

    /**
     * Get the trim material corresponding to the input.
     *
     * @param id The input
     * @return The trim material
     */
    @Nullable
    public String getTrimMaterial(String id) {
        var stack = get(id);
        if (VersionUtil.trimsSupported() && stack != null && stack.getItemMeta() instanceof ArmorMeta meta && meta.getTrim() != null) {
            return meta.getTrim().getMaterial().getKey().toString();
        }
        return null;
    }

    /**
     * Get the equippable slot corresponding to the input.
     *
     * @param id The input
     * @return The equippable slot
     */
    @Nullable
    public EquipmentSlot getEquippableSlot(String id) {
        var stack = get(id);
        if (VersionUtil.equippableSupported() && stack != null && stack.getItemMeta().hasEquippable()) {
            return stack.getItemMeta().getEquippable().getSlot();
        }
        return null;
    }

    /**
     * Get the equippable key corresponding to the input.
     *
     * @param id The input
     * @return The equippable key
     */
    @Nullable
    public NamespacedKey getEquippableModel(String id) {
        var stack = get(id);
        if (VersionUtil.equippableSupported() && stack != null && stack.getItemMeta().hasEquippable()) {
            return stack.getItemMeta().getEquippable().getModel();
        }
        return null;
    }

}
