package de.skyslycer.hmcwraps.preview.floating;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.preview.Preview;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.VectorUtil;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class FloatingPreview implements Preview {

    private final int entityId = SpigotReflectionUtil.generateEntityId();
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
        this.upsideDown = upsideDown;
        this.onClose = onClose;
        this.plugin = plugin;
    }

    public void preview() {
        player.closeInventory();

        sendSpawnPacket();
        sendMetadataPacket();
        sendTeleportPacket();
        sendEquipPacket();

        task = plugin.getFoliaLib().getImpl().runTimerAsync(new RotateRunnable(player, entityId, plugin), 3, 1);

        cancelTask = plugin.getFoliaLib().getImpl().runAtEntityLater(player, () -> plugin.getPreviewManager().remove(player.getUniqueId(), true),
                        plugin.getConfiguration().getPreview().getDuration() * 20L);
    }

    public void cancel(boolean open) {
        Optional.of(task).ifPresent(WrappedTask::cancel);
        Optional.of(cancelTask).ifPresent(WrappedTask::cancel);
        if (open && onClose != null) {
            onClose.accept(player);
        }
        plugin.getFoliaLib().getImpl().runAtEntityLater(player, () -> {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
            if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));
            }
        }, 1L);
    }

    private void sendSpawnPacket() {
        if (SpigotReflectionUtil.V_1_19_OR_HIGHER) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSpawnEntity(entityId,
                    Optional.of(UUID.randomUUID()),
                    EntityTypes.ARMOR_STAND,
                    VectorUtil.fromLocation(PlayerUtil.getOpposite(player)),
                    0f,
                    0f,
                    0f,
                    0,
                    Optional.empty()));
        } else {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSpawnLivingEntity(
                    entityId,
                    UUID.randomUUID(),
                    EntityTypes.ARMOR_STAND,
                    VectorUtil.fromLocation(PlayerUtil.getOpposite(player)),
                    0f,
                    0f,
                    0f,
                    VectorUtil.zeroVector(),
                    List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                            new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(upsideDown ? 0 : 180, 0, 0)),
                            new EntityData(5, EntityDataTypes.BOOLEAN, true))));
        }
    }

    private void sendMetadataPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(
                entityId,
                List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                        new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(upsideDown ? 0 : 180, 0, 0)),
                        new EntityData(5, EntityDataTypes.BOOLEAN, true)))
        );
    }

    private void sendTeleportPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerEntityTeleport(entityId, VectorUtil.fromLocation(PlayerUtil.getLookBlock(player)).subtract(0, upsideDown ? 1 : 0.5, 0),
                        0f, 0f, false));
    }

    private void sendEquipPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityEquipment(
                entityId, List.of(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(item)))));
    }

}
