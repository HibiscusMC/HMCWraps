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
        var twoBlocks = fixLocation(player.getEyeLocation().add(player.getLocation().getDirection().clone().multiply(2)), player);
        var oneBlock = fixLocation(player.getEyeLocation().add(player.getLocation().getDirection().clone()), player);
        if (oneBlock.getWorld().getBlockAt(oneBlock).getType() == Material.AIR) {
            if (twoBlocks.getWorld().getBlockAt(twoBlocks).getType() == Material.AIR) {
                return twoBlocks;
            }
            return oneBlock;
        }
        return fixLocation(player.getLocation(), player);
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
        return location;
    }

}
