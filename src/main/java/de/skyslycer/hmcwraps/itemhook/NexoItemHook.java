package de.skyslycer.hmcwraps.itemhook;

import com.nexomc.nexo.api.NexoItems;
import de.skyslycer.hmcwraps.util.VersionUtil;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

public class NexoItemHook implements ItemHook {

    @Override
    public String getPrefix() {
        return "nexo:";
    }

    @Override
    public ItemStack get(String id) {
        if (NexoItems.itemFromId(id) == null) {
            return null;
        }
        return NexoItems.itemFromId(id).build();
    }

    @Override
    public int getModelId(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta().hasCustomModelData()) {
            return stack.getItemMeta().getCustomModelData();
        }
        return -1;
    }

    @Override
    @Nullable
    public Color getColor(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            return meta.getColor();
        }
        return null;
    }

    @Override
    @Nullable
    public String getTrimPattern(String id) {
        var stack = get(id);
        if (VersionUtil.trimsSupported() && stack != null && stack.getItemMeta() instanceof ArmorMeta meta && meta.getTrim() != null) {
            return meta.getTrim().getPattern().getKey().toString();
        }
        return null;
    }

    @Override
    @Nullable
    public String getTrimMaterial(String id) {
        return "minecraft:redstone";
    }

}
