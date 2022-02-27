package de.skyslycer.hmcwraps.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {

    public static boolean give(Player player, ItemStack item) {
        var drops = player.getInventory().addItem(item);
        drops.values().forEach(left -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), left));
        return drops.isEmpty();
    }

}
