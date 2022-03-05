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
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.retrooper.packetevents.utils.SpigotDataHelper;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Preview {

    private final int entityId = Integer.MAX_VALUE - HMCWraps.RANDOM.nextInt(10000);
    private final Player player;
    private final ItemStack item;
    private final PaginatedGui gui;
    private final HMCWraps plugin;
    private BukkitTask task;
    private BukkitTask cancelTask;

    Preview(Player player, ItemStack item, PaginatedGui gui, HMCWraps plugin) {
        this.player = player;
        this.item = item;
        this.gui = gui;
        this.plugin = plugin;
    }

    public void preview() {
        if (PlayerUtil.getLookBlock(player) == null) {
            return;
        }
        player.getOpenInventory().close();
        PacketEvents.getAPI().getPlayerManager().sendPacketAsync(player, new WrapperPlayServerSpawnLivingEntity(
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
                        new EntityData(5, EntityDataTypes.BOOLEAN, true))
        ));
        PacketEvents.getAPI().getPlayerManager().sendPacketAsync(player, new WrapperPlayServerEntityMetadata(
                entityId,
                List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                        new EntityData(16, EntityDataTypes.ROTATION, new Vector3f(180, 0, 0)),
                        new EntityData(5, EntityDataTypes.BOOLEAN, true)))
        );
        PacketEvents.getAPI().getPlayerManager().sendPacketAsync(player,
                new WrapperPlayServerEntityTeleport(entityId, VectorUtils.fromLocation(PlayerUtil.getLookBlock(player)),
                        0f, 0f, false));
        PacketEvents.getAPI().getPlayerManager().sendPacketAsync(player, new WrapperPlayServerEntityEquipment(
                entityId, new Equipment(EquipmentSlot.HELMET, SpigotDataHelper.fromBukkitItemStack(item))));

        task = Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, new RotateRunnable(player, entityId, plugin), 0, 1);

        cancelTask = Bukkit.getScheduler()
                .runTaskLater(plugin, () -> cancel(true), plugin.getConfiguration().getPreview().getDuration() * 20L);
    }

    public void cancel(boolean open) {
        task.cancel();
        cancelTask.cancel();
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
        if (plugin.getConfiguration().getPreview().getSneakCancel().isActionBar()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(" "));
        }
        if (open) {
            gui.open(player);
        }
    }

}
