package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import de.skyslycer.hmcwraps.util.VectorUtils;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final Set<Point<Double>> locations;
    private final Location location;
    private Iterator<Point<Double>> iterator;

    private float currentAngle = 0;

    public RotateRunnable(Player player, Location location, int entityId, Set<Point<Double>> locations) {
        this.player = player;
        this.entityId = entityId;
        this.locations = locations;
        this.location = location;
        this.iterator = locations.iterator();
    }

    @Override
    public void run() {
        if (iterator.hasNext()) {
            var point = iterator.next();
            var appliedLocation = location.add(point.getX(), 0, point.getZ());
            PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                    new WrapperPlayServerEntityTeleport(entityId, VectorUtils.fromLocation(appliedLocation),
                            currentAngle, 90f, false));
        } else {
            iterator = locations.iterator();
            currentAngle = 0;
            run();
            return;
        }
        Bukkit.broadcastMessage("Current rotation: " + currentAngle);
        currentAngle += 1;
    }

}
