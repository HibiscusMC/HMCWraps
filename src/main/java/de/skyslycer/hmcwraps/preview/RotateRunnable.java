package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRotation;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.StringUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final HMCWraps plugin;

    private float currentAngle = 0;
    private double currentBobbing = 0;
    private boolean bobbingReversed = false;

    public RotateRunnable(Player player, int entityId, HMCWraps plugin) {
        this.player = player;
        this.entityId = entityId;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(player, plugin.getHandler().get(Messages.PREVIEW_BAR)));
        }
        if (plugin.getConfiguration().getPreview().getBobbing().isEnabled()) {
            if (currentBobbing >= plugin.getConfiguration().getPreview().getBobbing().getTimes()) {
                currentBobbing = 0;
                bobbingReversed = !bobbingReversed;
            }
            var movement = plugin.getConfiguration().getPreview().getBobbing().getMovement();
            PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                    new WrapperPlayServerEntityRelativeMoveAndRotation(entityId, 0d, bobbingReversed ? -movement : movement, 0d, currentAngle, 90f, false));
            currentBobbing++;
        } else {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityRotation(entityId, currentAngle, 90f, false));
        }
        currentAngle += plugin.getConfiguration().getPreview().getRotation();
    }

}
