package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.annotations.AnyPermission;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import dev.triumphteam.gui.guis.BaseGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Command("wraps")
public class WrapCommand {

    private static final String RELOAD_PERMISSION = "hmcwraps.commands.reload";
    private static final String CONVERT_PERMISSION = "hmcwraps.commands.convert";
    private static final String WRAP_PERMISSION = "hmcwraps.commands.wrap";
    private static final String WRAP_SELF_PERMISSION = "hmcwraps.commands.wrap.self";
    private static final String UNWRAP_PERMISSION = "hmcwraps.commands.unwrap";
    private static final String UNWRAP_SELF_PERMISSION = "hmcwraps.commands.unwrap.self";
    private static final String GIVE_WRAPPER_PERMISSION = "hmcwraps.commands.give.wrapper";
    private static final String GIVE_UNWRAPPER_PERMISSION = "hmcwraps.commands.give.unwrapper";
    private static final String PREVIEW_PERMISSION = "hmcwraps.commands.preview";
    private static final String LIST_PERMISSION = "hmcwraps.commands.list";
    public static final String WRAPS_PERMISSION = "hmcwraps.wraps";
    private static final String WRAPS_OPEN_PERMISSION = "hmcwraps.commands.open";
    private static final String WRAPS_DROP_PERMISSION = "hmcwraps.commands.drop";

    private final Set<String> confirmingPlayers = new HashSet<>();

    private final HMCWrapsPlugin plugin;

    public WrapCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("wraps")
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        if (plugin.getConfiguration().getPermissions().isInventoryPermission() && !player.hasPermission(WRAPS_PERMISSION)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION);
            return;
        }
        openWrapsInventory(player);
    }

    private void openWrapsInventory(Player player) {
        var item = player.getInventory().getItemInMainHand();
        var slot = player.getInventory().getHeldItemSlot();
        if (item.getType().isAir()) {
            if (plugin.getConfiguration().getInventory().isOpenWithoutItemEnabled()) {
                GuiBuilder.open(plugin, player, null, -1);
            } else {
                plugin.getMessageHandler().send(player, Messages.NO_ITEM);
            }
            return;
        }
        var type = item.getType();
        if (plugin.getWrapper().getWrap(item) != null && !plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(item).isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(item));
        }
        if (plugin.getCollectionHelper().getItems(type).isEmpty() || plugin.getWrapper().isGloballyDisabled(item)) {
            if (plugin.getConfiguration().getInventory().isOpenWithoutItemEnabled()) {
                GuiBuilder.open(plugin, player, null, -1);
            } else {
                plugin.getMessageHandler().send(player, Messages.NO_WRAPS);
            }
            return;
        }
        GuiBuilder.open(plugin, player, player.getInventory().getItem(slot), slot);
    }

    @Subcommand("open")
    @CommandPermission(WRAPS_OPEN_PERMISSION)
    @Description("Open the wraps inventory for another player.")
    public void onOpen(CommandSender sender, Player player) {
        openWrapsInventory(player);
        plugin.getMessageHandler().send(sender, Messages.COMMAND_OPEN, Placeholder.parsed("player", player.getName()));
    }

    @Subcommand("reload")
    @CommandPermission(RELOAD_PERMISSION)
    @Description("Reload configuration and messages.")
    public void onReload(CommandSender sender) {
        var current = System.nanoTime();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            plugin.getFoliaLib().getScheduler().runAtEntity(player, (ignored) -> {
                var topInventory = VersionUtil.getTopInventory(player);
                if (topInventory != null && topInventory.getHolder() instanceof BaseGui) {
                    player.closeInventory();
                }
            });
        }
        plugin.getFoliaLib().getScheduler().runAsync((ignored) -> {
            plugin.unload();
            plugin.load();
            plugin.getMessageHandler().send(sender, Messages.COMMAND_RELOAD,
                    Placeholder.parsed("time", String.format("%.2f", (System.nanoTime() - current) / 1_000_000.0)),
                    Placeholder.parsed("wraps", String.valueOf(plugin.getWrapsLoader().getWraps().size())),
                    Placeholder.parsed("collections", String.valueOf(plugin.getWrapsLoader().getCollections().size())));
        });
    }

    @Subcommand("convert")
    @CommandPermission(CONVERT_PERMISSION)
    @Description("Convert ItemSkins files to HMCWraps files.")
    public void onConvert(CommandSender sender, @Optional String confirm) {
        var uuid = "CONSOLE";
        if (sender instanceof Player player) {
            uuid = player.getUniqueId().toString();
        }
        if (confirm == null || !confirm.equalsIgnoreCase("confirm")) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_CONFIRM);
            confirmingPlayers.add(uuid);
        } else {
            if (!confirmingPlayers.contains(uuid)) {
                plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_NO_CONFIRM);
                return;
            }
            var success = plugin.getFileConverter().convertAll();
            if (!success) {
                plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_FAILED);
                return;
            }
            plugin.unload();
            plugin.load();
            plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_SUCCESS);
            confirmingPlayers.remove(uuid);
        }
    }

    @Subcommand("wrap")
    @Description("Wrap the item the player is holding in their main hand.")
    @AnyPermission({WRAP_PERMISSION, WRAP_SELF_PERMISSION})
    @AutoComplete("@wraps @players @actions")
    public void onWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Optional String actions) {
        if (player != sender && !sender.hasPermission(WRAP_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        if (wrap == null) {
            return;
        }
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        var matchingWrapPresent = plugin.getCollectionHelper().getItems(item.getType()).stream().anyMatch(wrap::equals);
        if (!matchingWrapPresent) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
            return;
        }
        item = plugin.getWrapper().setWrap(wrap, item, false, player);
        item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
        player.getInventory().setItemInMainHand(item);
        if (actions == null || !actions.equals("-actions")) {
            plugin.getActionHandler().pushWrap(wrap, player);
            plugin.getActionHandler().pushVirtualWrap(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_WRAP_WRAPPED);
    }

    @Subcommand("unwrap")
    @Description("Unwrap the item a player is holding in their main hand.")
    @AnyPermission({UNWRAP_PERMISSION, UNWRAP_SELF_PERMISSION})
    @AutoComplete("@players @actions")
    public void onUnwrap(CommandSender sender, @Default("self") Player player, @Optional String actions) {
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_WRAPPED);
            return;
        }
        item = plugin.getWrapper().removeWrap(item, player);
        player.getInventory().setItemInMainHand(item);
        if (actions == null || !actions.equals("-actions")) {
            plugin.getActionHandler().pushUnwrap(wrap, player);
            plugin.getActionHandler().pushVirtualUnwrap(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_UNWRAP_UNWRAPPED);
    }

    @Subcommand("preview")
    @Description("Preview a wrap for the specified player.")
    @CommandPermission(PREVIEW_PERMISSION)
    @AutoComplete("@wraps @players @actions")
    public void onPreview(CommandSender sender, Wrap wrap, @Default("self") Player player, @Optional String actions) {
        if (wrap == null) {
            return;
        }
        var material = plugin.getCollectionHelper().getMaterial(wrap);
        if (material == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NO_MATCHING_ITEM);
            return;
        }
        plugin.getPreviewManager().create(player, null, wrap);
        if (actions == null || !actions.equals("-actions")) {
            plugin.getActionHandler().pushPreview(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PREVIEW_CREATED);
    }

    @Subcommand("give wrapper")
    @Description("Give a wrapper to a player.")
    @CommandPermission(GIVE_WRAPPER_PERMISSION)
    @AutoComplete("@physicalWraps @players")
    public void onGiveWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Range(min = 1, max = 64) @Optional Integer amount) {
        var item = checkWrapper(wrap, sender, amount);
        if (item == null) {
            return;
        }
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalWrapper(item, wrap));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("drop")
    @Description("Drop a wrapper at the specified location.")
    @CommandPermission(WRAPS_DROP_PERMISSION)
    @AutoComplete("@physicalWraps")
    public void onDrop(CommandSender sender, Wrap wrap, double x, double y, double z, World world, @Optional Integer amount) {
        var item = checkWrapper(wrap, sender, amount);
        if (item == null || world == null) {
            return;
        }
        var location = new Location(world, x, y, z);
        location.getWorld().dropItemNaturally(location, plugin.getWrapper().setPhysicalWrapper(item, wrap));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_DROPPED_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @Description("Give an unwrapper to a player.")
    @CommandPermission(GIVE_UNWRAPPER_PERMISSION)
    @AutoComplete("@players")
    public void onGiveUnwrapper(CommandSender sender, @Default("self") Player player, @Optional @Range(min = 1, max = 64) Integer amount) {
        var item = plugin.getConfiguration().getUnwrapper().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalUnwrapper(item));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_UNWRAPPER);
    }

    @Subcommand("list")
    @Description("Shows all wraps and collections configured.")
    @CommandPermission(LIST_PERMISSION)
    public void onList(CommandSender sender) {
        var handler = plugin.getMessageHandler();
        var set = new ArrayList<Component>();
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_HEADER)));
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS)));
        plugin.getWrapsLoader().getCollections().forEach((key, list) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", key)));
            list.forEach(entry -> set.add(
                    StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS_FORMAT), Placeholder.parsed("value", entry))));
        });
        set.add(Component.space());
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS)));
        plugin.getWrapsLoader().getTypeWraps().forEach((material, wrapIds) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", material)));
            wrapIds.forEach((wrapId) -> {
                var wrap = plugin.getWrapsLoader().getWraps().get(wrapId);
                var uuid = wrap.getUuid();
                var placeholders = List.of(Placeholder.parsed("value", uuid), Placeholder.parsed("permission", wrap.getPermission() == null ? "None" : wrap.getPermission()),
                        Placeholder.parsed("modelid", String.valueOf(wrap.getModelId())),
                        Placeholder.parsed("player", sender instanceof Player player ? player.getName() : " "),
                        Placeholder.parsed("physical", String.valueOf(wrap.getPhysical() != null)),
                        Placeholder.parsed("preview", String.valueOf(wrap.isPreview())));
                set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS_FORMAT), placeholders.toArray(Single[]::new)));
            });
        });
        var component = Component.empty();
        for (Component entry : set) {
            component = component.append(entry).append(Component.newline());
        }
        sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(component));
    }

    private ItemStack checkWrapper(Wrap wrap, CommandSender sender, Integer amount) {
        if (wrap == null) {
            return null;
        }
        if (wrap.getPhysical() == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return null;
        }
        var item = wrap.getPhysical().toItem(plugin, null);
        item.setAmount(amount == null ? 1 : amount);
        return item;
    }

    @Subcommand("help")
    @Description("Shows the help page.")
    public void onHelp(CommandSender sender, CommandHelp<String> helpEntries) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_HEADER);
        Supplier<Stream<String>> filteredEntries = () -> helpEntries.paginate(1, 100).stream().filter(string -> !string.isEmpty());
        if (filteredEntries.get().findAny().isEmpty()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_NO_PERMISSION);
        } else {
            filteredEntries.get().forEach((line) -> StringUtil.send(sender, line));
        }
    }

}
