package de.skyslycer.hmcwraps.direction;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum Direction {

    NORTH(0, 1),
    EAST(1, -1),
    SOUTH(0, -1),
    WEST(-1, 1);

    private final int x;
    private final int z;

    Direction(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static Location apply(Player player) {
        var yaw = player.getLocation().getYaw();
        Direction direction;
        if (yaw >= -45 && yaw <= 45) {
            direction = SOUTH;
        } else if (yaw > 45 && yaw < 135) {
            direction = WEST;
        } else if (yaw > -135 || yaw < -45) {
            direction = EAST;
        } else {
            direction = NORTH;
        }

        return player.getLocation().getBlock().getLocation().clone().add(direction.x, 0, direction.z);
    }

}
