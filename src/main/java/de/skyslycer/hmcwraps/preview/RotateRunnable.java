package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.circle.Point;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.StringUtil;
import java.util.Iterator;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final HMCWraps plugin;

    private float currentAngle = 0;
    private double lastLocation = 0d;
    private Iterator<Point> iterator;

    public RotateRunnable(Player player, int entityId, HMCWraps plugin) {
        this.player = player;
        this.entityId = entityId;
        this.plugin = plugin;
        if (plugin.getConfiguration().getPreview().getBobbing().isEnabled() && plugin.getCircleManager().getLocations().containsKey(plugin.getConfiguration().getPreview().getBobbing())) {
            iterator = plugin.getCircleManager().getLocations().get(plugin.getConfiguration().getPreview().getBobbing()).iterator();
        }
    }

    @Override
    public void run() {
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(player, plugin.getHandler().get(Messages.PREVIEW_BAR)));
        }
        if (iterator != null) {
            if (iterator.hasNext()) {
                var y = iterator.next().getY();
                System.out.println(y - lastLocation);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                        new WrapperPlayServerEntityRelativeMoveAndRotation(entityId, 0d, y - lastLocation, 0d, currentAngle, 90f, false));
                lastLocation = y;
            } else {
                if (plugin.getConfiguration().getPreview().getBobbing().isEnabled() && plugin.getCircleManager().getLocations().containsKey(plugin.getConfiguration().getPreview().getBobbing())) {
                    iterator = plugin.getCircleManager().getLocations().get(plugin.getConfiguration().getPreview().getBobbing()).iterator();
                    run();
                }
            }
        } else {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityRotation(entityId, currentAngle, 90f, false));
        }
        currentAngle += plugin.getConfiguration().getPreview().getRotation();
    }

}
