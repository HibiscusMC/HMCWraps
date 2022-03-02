package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final Set<Point<Double>> locations;
    private Iterator<Point<Double>> iterator;

    private float currentAngle = 0;

    public RotateRunnable(Player player, int entityId, Set<Point<Double>> locations) {
        this.player = player;
        this.entityId = entityId;
        this.locations = locations;
        this.iterator = locations.iterator();
    }

    @Override
    public void run() {
        if (iterator.hasNext()) {
            var point = iterator.next();
            currentAngle -= 5;
            PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                    new WrapperPlayServerEntityRelativeMoveAndRotation(entityId, point.getX(), 0, point.getZ(),
                            currentAngle, 90f, false));
        } else {
            iterator = locations.iterator();
            run();
        }
    }

}
