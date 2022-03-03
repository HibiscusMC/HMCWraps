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
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.direction.Direction;
import de.skyslycer.hmcwraps.util.VectorUtils;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.retrooper.packetevents.utils.SpigotDataHelper;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class Preview {

    private static final double RADIUS = 1d;
    private static final byte MASK = 0x00;

    private final int entityId = Integer.MAX_VALUE - HMCWraps.RANDOM.nextInt(10000);
    private final Player player;
    private final ItemStack item;
    private final PaginatedGui gui;
    private final Set<Point<Double>> locations = new LinkedHashSet<>();
    private BukkitTask task;

    public Preview(Player player, ItemStack item, PaginatedGui gui) {
        this.player = player;
        this.item = item;
        this.gui = gui;
    }

    public void preview(HMCWraps plugin) {
        var location = Direction.apply(player);
        player.getOpenInventory().close();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            generateCircleLocations();
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSpawnLivingEntity(
                    entityId,
                    UUID.randomUUID(),
                    EntityTypes.ARMOR_STAND,
                    VectorUtils.fromLocation(location),
                    0f,
                    0f,
                    0f,
                    VectorUtils.zeroVector(),
                    List.of(new EntityData(0, EntityDataTypes.BYTE, (byte) 0x20),
                            new EntityData(15, EntityDataTypes.BYTE, (byte) 0x10),
                            new EntityData(15, EntityDataTypes.BYTE, (byte) 0x04),
                            new EntityData(19, EntityDataTypes.ROTATION, new Vector3f(-180, -180, -180)),
                            new EntityData(5, EntityDataTypes.BOOLEAN, true)))
            );
            PacketEvents.getAPI().getPlayerManager()
                    .sendPacket(player, new WrapperPlayServerEntityEquipment(entityId, new Equipment(
                            EquipmentSlot.MAINHAND, SpigotDataHelper.fromBukkitItemStack(item))));

            task = Bukkit.getScheduler()
                    .runTaskTimerAsynchronously(plugin, new RotateRunnable(player, location, entityId, locations), 0, 20);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                task.cancel();
                PacketEvents.getAPI().getPlayerManager()
                        .sendPacket(player, new WrapperPlayServerDestroyEntities(entityId));
                gui.open(player);
            }, 30 * 20);
        });
    }

    private void generateCircleLocations() {
        for (int i = 0; i < 360; i++) {
            double angle = i * 180 / Math.PI;
            locations.add(Point.build(Math.cos(angle) * RADIUS, Math.sin(angle) * RADIUS));
        }
    }

}
