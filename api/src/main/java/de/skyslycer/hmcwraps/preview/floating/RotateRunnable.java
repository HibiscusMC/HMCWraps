package de.skyslycer.hmcwraps.preview.floating;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.MathUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;

public class RotateRunnable implements Runnable {

    private final Player player;
    private final int entityId;
    private final HMCWraps plugin;

    private int currentSinAngle = 0;
    private float currentAngle = 0;
    private double oldHeight = 0;

    public RotateRunnable(Player player, int entityId, HMCWraps plugin) {
        this.player = player;
        this.entityId = entityId;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar() && plugin.getConfiguration().getPreview().getSneakCancel().isEnabled()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(player, plugin.getMessageHandler().get(Messages.PREVIEW_BAR)));
        }
        if (plugin.getConfiguration().getPreview().getBobbing().isEnabled()) {
            var newHeight = MathUtil.sin(currentSinAngle) * plugin.getConfiguration().getPreview().getBobbing()
                    .getIntensity();
            var difference = newHeight - oldHeight;
            oldHeight = newHeight;
            VersionUtil.sendRelativeMoveAndRotatePacket(player, entityId, difference, currentAngle);
            currentSinAngle += plugin.getConfiguration().getPreview().getBobbing().getSpeed();
            if (currentSinAngle > 360) {
                currentSinAngle = 0;
            }
        } else {
            VersionUtil.sendRelativeMoveAndRotatePacket(player, entityId, 0, currentAngle);
        }
        currentAngle += plugin.getConfiguration().getPreview().getRotation();
    }

}
