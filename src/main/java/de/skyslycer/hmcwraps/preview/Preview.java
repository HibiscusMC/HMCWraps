package de.skyslycer.hmcwraps.preview;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.VectorUtils;
import dev.triumphteam.gui.guis.BaseGui;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Preview {

    private final int entityId = SpigotReflectionUtil.generateEntityId();
    private final Player player;
    private final ItemStack item;
    private final BaseGui gui;
    private final HMCWraps plugin;
    private BukkitTask task;
    private BukkitTask cancelTask;

    Preview(Player player, ItemStack item, BaseGui gui, HMCWraps plugin) {
        this.player = player;
        this.item = item;
        this.gui = gui;
        this.plugin = plugin;
    }

    public void preview() {
        if (PlayerUtil.getLookBlock(player) == null) {
            return;
        }
        if (gui != null) {
            gui.close(player);
        }

        sendSpawnPacket();
        sendMetadataPacket();
        sendTeleportPacket();
        sendEquipPacket();

        task = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, new RotateRunnable(player, entityId, plugin), 0, 1);

        cancelTask = Bukkit.getScheduler()
                .runTaskLater(plugin, () -> plugin.getPreviewManager().remove(player.getUniqueId(), true), plugin.getConfiguration().getPreview().getDuration() * 20L);
    }

    public void cancel(boolean open) {
        task.cancel();
        cancelTask.cancel();
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));
        }
        if (open && gui != null) {
            gui.open(player);
        }
    }

    private void sendSpawnPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSpawnLivingEntity(
                entityId,
                UUID.randomUUID(),
                EntityTypes.ARMOR_STAND,
                VectorUtils.fromLocation(PlayerUtil.getOpposite(player)),
                0f,
                0f,
                0f,
                VectorUtils.zeroVector(),
                List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                        new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(180, 0, 0)),
                        new EntityData(5, EntityDataTypes.BOOLEAN, true))));
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
                new WrapperPlayServerEntityTeleport(entityId, VectorUtils.fromLocation(PlayerUtil.getLookBlock(player)),
                        0f, 0f, false));
    }

    private void sendEquipPacket() {
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityEquipment(
                entityId, List.of(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(item)))));
    }

}
