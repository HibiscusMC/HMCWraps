package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.annotations.NoHelp;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@NoHelp
@Command("wraps")
public class TestCommand {

    public static final String DEBUG_PERMISSION = "hmcwraps.debug";

    private final HMCWrapsPlugin plugin;

    public TestCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("test invalidcolors")
    @Description("Tests the invalid color codes in the plugin.")
    @CommandPermission(DEBUG_PERMISSION)
    public void onTestInvalidColors(Player player) {
        var item = new ItemStack(Material.DIAMOND_SWORD);
        var meta = item.getItemMeta();
        meta.setLore(List.of("§zthere is a preceeding invalid color code"));
        item.setItemMeta(meta);
        player.getInventory().addItem(item);
        StringUtil.send(player, "<white>[TEST] Received an item with an invalid color code in its lore.");
    }

    @Subcommand("test reflection")
    @Description("Tests the reflection version helper methods.")
    @AutoComplete("@players")
    @CommandPermission(DEBUG_PERMISSION)
    public void onTestReflection(CommandSender sender, Player player) {
        StringUtil.send(sender, "<white>[TEST] Self testing reflection methods for functionality...");
        StringUtil.send(sender, "<white>[TEST] Commencing with attribute tests...");
        try {
            VersionUtil.getOpenInventoryType(player);
            StringUtil.send(sender, "<green>[OK] <gray>Method getOpenInventoryType passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method getOpenInventoryType failed.");
            plugin.getLogger().log(Level.SEVERE, "getOpenInventoryType test failed:", e);
        }
        try {
            VersionUtil.getBottomInventory(player);
            StringUtil.send(sender, "<green>[OK] <gray>Method getBottomInventory passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method getBottomInventory failed.");
            plugin.getLogger().log(Level.SEVERE, "getBottomInventory test failed:", e);
        }
        try {
            VersionUtil.getTopInventory(player);
            StringUtil.send(sender, "<green>[OK] <gray>Method getTopInventory passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method getTopInventory failed.");
            plugin.getLogger().log(Level.SEVERE, "getTopInventory test failed:", e);
        }
        try {
            var testEvent = new InventoryClickEvent(player.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ALL);
            VersionUtil.getItemFromSlot(testEvent, 0);
            StringUtil.send(sender, "<green>[OK] <gray>Method getItemFromSlot passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method getItemFromSlot failed.");
            plugin.getLogger().log(Level.SEVERE, "getItemFromSlot test failed:", e);
        }
        try {
            var testEvent = new InventoryClickEvent(player.getOpenInventory(), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, InventoryAction.PICKUP_ALL);
            VersionUtil.setItemInSlot(testEvent, 0, new ItemStack(Material.DIAMOND));
            StringUtil.send(sender, "<green>[OK] <gray>Method setItemInSlot passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method setItemInSlot failed.");
            plugin.getLogger().log(Level.SEVERE, "setItemInSlot test failed:", e);
        }
        try {
            var testStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
            VersionUtil.Attribute.addAttributeModifier(testStack.getItemMeta(), EquipmentSlot.CHEST, VersionUtil.Attribute.ARMOR_TOUGHNESS, 1.0);
            StringUtil.send(sender, "<green>[OK] <gray>Method addAttributeModifier passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method addAttributeModifier failed.");
            plugin.getLogger().log(Level.SEVERE, "addAttributeModifier test failed:", e);
        }
        try {
            var testStack = new ItemStack(Material.DIAMOND_CHESTPLATE);
            VersionUtil.Attribute.removeAttributeModifier(testStack.getItemMeta(), VersionUtil.Attribute.ARMOR_TOUGHNESS);
            StringUtil.send(sender, "<green>[OK] <gray>Method removeAttributeModifier passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method removeAttributeModifier failed.");
            plugin.getLogger().log(Level.SEVERE, "removeAttributeModifier test failed:", e);
        }
        StringUtil.send(sender, "<white>[TEST] Commencing with NMS packet tests...");
        try {
            VersionUtil.sendFakeItem(player, new ItemStack(Material.DIAMOND), 0);
            StringUtil.send(sender, "<green>[OK] <gray>Method sendFakeItem passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendFakeItem failed.");
            plugin.getLogger().log(Level.SEVERE, "sendFakeItem test failed:", e);
        }
        var entityId = ThreadLocalRandom.current().nextInt();
        try {
            entityId = VersionUtil.getNextEntityId();
            StringUtil.send(sender, "<green>[OK] <gray>Method getNextEntityId passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method getNextEntityId failed.");
            plugin.getLogger().log(Level.SEVERE, "getNextEntityId test failed:", e);
        }
        try {
            VersionUtil.sendSpawnPacket(player, entityId, false);
            StringUtil.send(sender, "<green>[OK] <gray>Method sendSpawnPacket passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendSpawnPacket failed.");
            plugin.getLogger().log(Level.SEVERE, "sendSpawnPacket test failed:", e);
        }
        try {
            VersionUtil.sendMetadataPacket(player, entityId, false);
            StringUtil.send(sender, "<green>[OK] <gray>Method sendMetadataPacket passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendMetadataPacket failed.");
            plugin.getLogger().log(Level.SEVERE, "sendMetadataPacket test failed:", e);
        }
        try {
            VersionUtil.sendTeleportPacket(player, entityId, false);
            StringUtil.send(sender, "<green>[OK] <gray>Method sendTeleportPacket passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendTeleportPacket failed.");
            plugin.getLogger().log(Level.SEVERE, "sendTeleportPacket test failed:", e);
        }
        try {
            VersionUtil.sendEquipPacket(player, entityId, new ItemStack(Material.DIAMOND_SWORD));
            StringUtil.send(sender, "<green>[OK] <gray>Method sendEquipPacket passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendEquipPacket failed.");
            plugin.getLogger().log(Level.SEVERE, "sendEquipPacket test failed:", e);
        }
        try {
            VersionUtil.sendRelativeMoveAndRotatePacket(player, entityId, 1, 0);
            StringUtil.send(sender, "<green>[OK] <gray>Method sendRelativeMoveAndRotatePacket passed.");
        } catch (Exception e) {
            StringUtil.send(sender, "<red>[FAIL] <gray>Method sendRelativeMoveAndRotatePacket failed.");
            plugin.getLogger().log(Level.SEVERE, "sendRelativeMoveAndRotatePacket test failed:", e);
        }
        int finalEntityId = entityId;
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
            try {
                VersionUtil.sendDestroyPacket(player, finalEntityId);
                StringUtil.send(sender, "<green>[OK] <gray>Method sendDestroyPacket passed.");
            } catch (Exception e) {
                StringUtil.send(sender, "<red>[FAIL] <gray>Method sendDestroyPacket failed.");
                plugin.getLogger().log(Level.SEVERE, "sendDestroyPacket test failed:", e);
            }
            StringUtil.send(sender, "<white>[TEST] Tests concluded. Please check the console for any errors.");
        }, 20L);
    }

}
