package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.MathUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.VectorUtils;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final HMCWraps plugin;
    private final Location itemLocation;

    private int currentSinAngle = 0;
    private float currentAngle = 0;

    public RotateRunnable(Player player, int entityId, Location itemLocation, HMCWraps plugin) {
        this.player = player;
        this.entityId = entityId;
        this.plugin = plugin;
        this.itemLocation = itemLocation;
    }

    @Override
    public void run() {
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(player, plugin.getHandler().get(Messages.PREVIEW_BAR)));
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityRotation(entityId, currentAngle, 90f, false));
        currentAngle += plugin.getConfiguration().getPreview().getRotation();
        if (plugin.getConfiguration().getPreview().getBobbing().isEnabled()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                    new WrapperPlayServerEntityTeleport(
                            entityId,
                            VectorUtils.fromLocation(itemLocation.clone().add(0, MathUtil.sin(currentSinAngle) * plugin.getConfiguration().getPreview().getBobbing()
                                    .getIntensity(), 0)),
                            0f, 0f, false)
            );
            currentSinAngle += plugin.getConfiguration().getPreview().getBobbing().getSpeed();
            if (currentSinAngle > 360) {
                currentSinAngle = 0;
            }
        }
    }

}
