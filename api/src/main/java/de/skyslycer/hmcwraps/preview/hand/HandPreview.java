package de.skyslycer.hmcwraps.preview.hand;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.preview.Preview;
import de.skyslycer.hmcwraps.util.StringUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

public class HandPreview implements Preview {

    private final Player player;
    private final ItemStack item;
    private final Consumer<Player> onClose;
    private final HMCWraps plugin;
    private WrappedTask task;
    private WrappedTask cancelTask;
    private ItemStack oldItem;
    private int slot = 0;

    public HandPreview(Player player, ItemStack item, Consumer<Player> onClose, HMCWraps plugin) {
        this.player = player;
        this.item = item;
        this.onClose = onClose;
        this.plugin = plugin;
    }

    public void preview() {
        player.closeInventory();

        oldItem = player.getInventory().getItemInMainHand();
        slot = 36 + player.getInventory().getHeldItemSlot();
        sendFakeItem(item);

        task = plugin.getFoliaLib().getScheduler().runTimerAsync(() -> {
            if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar() && plugin.getConfiguration().getPreview().getSneakCancel().isEnabled()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(player, plugin.getMessageHandler().get(Messages.PREVIEW_BAR)));
            }
        }, 3, 1);
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
            sendFakeItem(oldItem);
            if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));
            }
        }, 1L);
    }

    private void sendFakeItem(ItemStack item) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerSetSlot(0, -1, slot, SpigotReflectionUtil.decodeBukkitItemStack(item)));
    }

}
