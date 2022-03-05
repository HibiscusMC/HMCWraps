package de.skyslycer.hmcwraps.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {

    public static void give(Player player, ItemStack item) {
        var drops = player.getInventory().addItem(item);
        drops.values().forEach(left -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), left));
    }

    public static Location getLookBlock(Player player) {
        var blocks = player.getLineOfSight(null, 2);
        if (blocks.size() < 2 || blocks.get(1).getType() != Material.AIR) {
            return null;
        }
        return fixLocation(blocks.get(1).getLocation().clone(), player);
    }

    public static Location getOpposite(Player player) {
        var facing = player.getFacing().getOppositeFace();
        var location = player.getLocation().clone();
        location.add(facing.getModX(), facing.getModY(), facing.getModZ());
        var block = player.getLocation().getWorld().getBlockAt(location);
        return block.getLocation();
    }

    private static Location fixLocation(Location location, Player player) {
        location.setY(player.getLocation().getY() + 1);
        location.add(0.5, 0, 0.5);
        return location;
    }

}
