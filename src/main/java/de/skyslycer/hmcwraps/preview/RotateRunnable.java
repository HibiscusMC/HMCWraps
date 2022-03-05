package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
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

    public RotateRunnable(Player player, int entityId, HMCWraps plugin) {
        this.player = player;
        this.entityId = entityId;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    StringUtil.parse(player, plugin.getHandler().get(Messages.PREVIEW_BAR)));
        }
        PacketEvents.getAPI().getPlayerManager()
                .sendPacket(player, new WrapperPlayServerEntityRotation(entityId, currentAngle, 90f, false));
        currentAngle += plugin.getConfiguration().getPreview().getRotation();
    }

}
