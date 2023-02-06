package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import de.skyslycer.hmcwraps.IHMCWraps;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.VectorUtil;
import dev.triumphteam.gui.guis.BaseGui;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Preview {

    private final int entityId = SpigotReflectionUtil.generateEntityId();
    private final Player player;
    private final ItemStack item;
    private final BaseGui gui;
    private final IHMCWraps plugin;
    private BukkitTask task;
    private BukkitTask cancelTask;

    Preview(Player player, ItemStack item, BaseGui gui, IHMCWraps plugin) {
        this.player = player;
        this.item = item;
        this.gui = gui;
        this.plugin = plugin;
    }

    /**
     * Start the preview.
     */
    public void preview() {
        if (gui != null) {
            gui.close(player);
        }

        sendSpawnPacket();
        sendMetadataPacket();
        sendTeleportPacket();
        sendEquipPacket();

        task = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, new RotateRunnable(player, entityId, plugin), 3, 1);

        cancelTask = Bukkit.getScheduler()
                .runTaskLater(plugin, () -> plugin.getPreviewManager().remove(player.getUniqueId(), true),
                        plugin.getConfiguration().getPreview().getDuration() * 20L);
    }

    /**
     * Cancel the preview.
     *
     * @param open If the inventory should open again
     */
    public void cancel(boolean open) {
        task.cancel();
        cancelTask.cancel();
        if (open && gui != null) {
            gui.open(player);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
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
            PacketEvents.getAPI().getPlayerManager()
                    .sendPacket(player, new WrapperPlayServerEntityMetadata(entityId,
                            List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                                    new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(180, 0, 0)),
                                    new EntityData(5, EntityDataTypes.BOOLEAN, true))));
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
                            new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(180, 0, 0)),
                            new EntityData(5, EntityDataTypes.BOOLEAN, true))));
        }
    }

    private void sendMetadataPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(
                entityId,
                List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                        new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(180, 0, 0)),
                        new EntityData(5, EntityDataTypes.BOOLEAN, true)))
        );
    }

    private void sendTeleportPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player,
                new WrapperPlayServerEntityTeleport(entityId, VectorUtil.fromLocation(PlayerUtil.getLookBlock(player)),
                        0f, 0f, false));
    }

    private void sendEquipPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityEquipment(
                entityId, List.of(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(item)))));
    }

}
