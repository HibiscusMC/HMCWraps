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

import java.util.logging.Level;

@NoHelp
@Command("wraps")
public class TestCommand {

    public static final String DEBUG_PERMISSION = "hmcwraps.debug";

    private final HMCWrapsPlugin plugin;

    public TestCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Subcommand("test reflection")
    @Description("Tests the reflection version helper methods.")
    @AutoComplete("@players")
    @CommandPermission(DEBUG_PERMISSION)
    public void onTestReflection(CommandSender sender, Player player) {
        StringUtil.send(sender, "<white>[TEST] <gray>Self testing reflection methods for functionality...");
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
        StringUtil.send(sender, "<white>[TEST] <gray>Tests concluded. Please check the console for any errors.");
    }

}
