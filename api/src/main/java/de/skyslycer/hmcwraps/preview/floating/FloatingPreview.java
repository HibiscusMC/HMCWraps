package de.skyslycer.hmcwraps.preview.floating;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.preview.Preview;
import de.skyslycer.hmcwraps.util.VersionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

public class FloatingPreview implements Preview {

    private final int entityId = VersionUtil.getNextEntityId();
    private final Player player;
    private final ItemStack item;
    private final Consumer<Player> onClose;
    private final HMCWraps plugin;
    private final boolean upsideDown;
    private WrappedTask task;
    private WrappedTask cancelTask;

    public FloatingPreview(Player player, ItemStack item, boolean upsideDown, Consumer<Player> onClose, HMCWraps plugin) {
        this.player = player;
        this.item = item;
        this.upsideDown = item.getType().toString().contains("_HELMET") ? !upsideDown : upsideDown;
        this.onClose = onClose;
        this.plugin = plugin;
    }

    public void preview() {
        player.closeInventory();

        VersionUtil.sendSpawnPacket(player, entityId, upsideDown);
        VersionUtil.sendMetadataPacket(player, entityId, upsideDown);
        VersionUtil.sendTeleportPacket(player, entityId, upsideDown);
        VersionUtil.sendEquipPacket(player, entityId, item);

        task = plugin.getFoliaLib().getScheduler().runTimerAsync(new RotateRunnable(player, entityId, plugin), 0, 1);

        cancelTask = plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> plugin.getPreviewManager().remove(player.getUniqueId(), true),
                        plugin.getConfiguration().getPreview().getDuration() * 20L);
    }

    public void cancel(boolean open) {
        Optional.of(task).ifPresent(WrappedTask::cancel);
        Optional.of(cancelTask).ifPresent(WrappedTask::cancel);
        if (open && onClose != null) {
            onClose.accept(player);
        }
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
            VersionUtil.sendDestroyPacket(player, entityId);
            if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(" "));
            }
        }, 1L);
    }

}
