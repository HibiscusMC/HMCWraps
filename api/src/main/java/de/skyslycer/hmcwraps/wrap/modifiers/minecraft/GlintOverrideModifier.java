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

public class GlintOverrideModifier implements WrapModifier {

    private final NamespacedKey originalGlintKey;

    private final HMCWraps plugin;

    public GlintOverrideModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalGlintKey = new NamespacedKey(plugin, "original-glint");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var originalGlint = getOriginalGlint(item);
        var currentGlint = VersionUtil.hasDataComponents() && meta.hasEnchantmentGlintOverride() ? meta.getEnchantmentGlintOverride() : null;
        if (currentWrap != null && VersionUtil.hasDataComponents()) {
            meta.setEnchantmentGlintOverride(originalGlint);
        }
        if (VersionUtil.hasDataComponents()) {
            if (wrap != null) {
                if (wrap.isGlintOverride() != null) {
                    meta.setEnchantmentGlintOverride(wrap.isGlintOverride());
                }
            } else {
                meta.setEnchantmentGlintOverride(originalGlint);
            }
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalGlint(item, currentGlint);
        }
    }

    private void setOriginalGlint(ItemStack item, Boolean glint) {
        var meta = item.getItemMeta();
        if (glint != null) {
            meta.getPersistentDataContainer().set(originalGlintKey, PersistentDataType.BOOLEAN, glint);
        } else {
            meta.getPersistentDataContainer().remove(originalGlintKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original glint override of the item.
     *
     * @param item The item
     * @return The original glint override
     */
    public Boolean getOriginalGlint(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalGlintKey, PersistentDataType.BOOLEAN);
    }

}
