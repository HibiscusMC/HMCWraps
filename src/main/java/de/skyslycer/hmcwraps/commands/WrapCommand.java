package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.permission.PermissionHelper;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

@Command("wraps")
public class WrapCommand {

    public static final String ALL_PERMISSION = "hmcwraps.admin";
    public static final String VIRTUAL_PERMISSION = "hmcwraps.commands.virtual";
    public static final String PHYSICAL_PERMISSION = "hmcwraps.commands.physical";
    public static final String MANAGEMENT_PERMISSION = "hmcwraps.commands.management";
    public static final String[] RELOAD_PERMISSION = {"hmcwraps.command.reload", ALL_PERMISSION};
    public static final String[] WRAP_PERMISSION = {"hmcwraps.command.wrap", ALL_PERMISSION, MANAGEMENT_PERMISSION, VIRTUAL_PERMISSION};
    public static final String[] UNWRAP_PERMISSION = {"hmcwraps.command.unwrap", ALL_PERMISSION, MANAGEMENT_PERMISSION, VIRTUAL_PERMISSION};
    public static final String[] GIVE_WRAPPER_PERMISSION = {"hmcwraps.command.give.wrapper", ALL_PERMISSION, MANAGEMENT_PERMISSION, PHYSICAL_PERMISSION};
    public static final String[] GIVE_UNWRAPPER_PERMISSION = {"hmcwraps.command.give.unwrapper", ALL_PERMISSION, MANAGEMENT_PERMISSION, PHYSICAL_PERMISSION};
    public static final String[] PREVIEW_PERMISSION = {"hmcwraps.command.preview", ALL_PERMISSION, MANAGEMENT_PERMISSION};
    public static final String[] LIST_PERMISSION = {"hmcwraps.command.list", ALL_PERMISSION, MANAGEMENT_PERMISSION};
    public static final String[] WRAPS_PERMISSION = {"hmcwraps.wraps", ALL_PERMISSION, MANAGEMENT_PERMISSION};

    private final HMCWraps plugin;

    public WrapCommand(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        if (plugin.getConfiguration().getPermissionSettings().isInventoryPermission() && !PermissionHelper.hasAnyPermission(player, WRAPS_PERMISSION)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.getMessageHandler().send(player, Messages.NO_ITEM);
            return;
        }
        if (plugin.getCollectionHelper().getItems(item.getType()).isEmpty()) {
            plugin.getMessageHandler().send(player, Messages.NO_WRAPS);
            return;
        }
        GuiBuilder.open(plugin, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
    }

    @Subcommand("reload")
    @CommandPermission("hmcwraps.admin")
    @Description("Reload configuration and messages.")
    public void onReload(CommandSender sender) {
        if (!PermissionHelper.hasAnyPermission(sender, RELOAD_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        plugin.unload();
        plugin.load();
        plugin.getMessageHandler().send(sender, Messages.COMMAND_RELOAD);
    }

    @Subcommand("wrap")
    @Description("Wrap the item a player is holding in their main hand.")
    @AutoComplete("@wraps * *")
    public void onWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Switch @Default("true") boolean actions) {
        if (!PermissionHelper.hasAnyPermission(sender, WRAP_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType() == Material.AIR) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        for (IWrappableItem wrappableItem : plugin.getCollectionHelper().getItems(item.getType())) {
            if (wrappableItem.getWraps().containsValue(wrap)) {
                item = plugin.getWrapper().setWrap(wrap, item, false, player, true);
                item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
                player.getInventory().setItemInMainHand(item);
                if (actions) {
                    plugin.getActionHandler().pushWrap(wrap, player);
                }
                plugin.getMessageHandler().send(sender, Messages.COMMAND_WRAP_WRAPPED);
                return;
            }
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
    }

    @Subcommand("unwrap")
    @Description("Unwrap the item a player is holding in their main hand.")
    @AutoComplete("@players *")
    public void onUnwrap(CommandSender sender, @Default("self") Player player, @Switch @Default("true") boolean actions) {
        if (!PermissionHelper.hasAnyPermission(sender, UNWRAP_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType() == Material.AIR) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_WRAPPED);
            return;
        }
        item = plugin.getWrapper().removeWrap(item, player, true);
        player.getInventory().setItemInMainHand(item);
        if (actions) {
            plugin.getActionHandler().pushUnwrap(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_UNWRAP_UNWRAPPED);
    }

    @Subcommand("preview")
    @Description("Preview a wrap for the specified player.")
    @AutoComplete("@wraps *")
    public void onPreview(CommandSender sender, Wrap wrap, @Default("self") Player player, @Switch @Default("true") boolean actions) {
        if (!PermissionHelper.hasAnyPermission(sender, PREVIEW_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        var currentCollection = "";
        var itemMaterial = Material.AIR;
        for (Map.Entry<String, IWrappableItem> entry : plugin.getWrappableItems().entrySet()) {
            currentCollection = entry.getKey();
            if (entry.getValue().getWraps().containsValue(wrap)) {
                break;
            }
        }
        if (currentCollection.equals("")) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_INVALID_WRAP);
            return;
        }

        if (Material.getMaterial(currentCollection) != null) {
            itemMaterial = Material.getMaterial(currentCollection);
        } else if (plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().isPresent()) {
            itemMaterial = plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().get();
        } else {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NO_MATCHING_ITEM);
            return;
        }
        plugin.getPreviewManager().create(player, ItemBuilder.from(itemMaterial).model(wrap.getModelId()).build(), null);
        if (actions) {
            plugin.getActionHandler().pushPreview(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PREVIEW_CREATED);
    }

    @Subcommand("give wrapper")
    @Description("Give a wrapper to a player.")
    @AutoComplete("@physicalWraps * *")
    public void onGiveWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Range(min = 1, max = 64) @Optional Integer amount) {
        if (!PermissionHelper.hasAnyPermission(sender, GIVE_WRAPPER_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        if (wrap.getPhysical().isEmpty()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return;
        }
        var item = wrap.getPhysical().get().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalWrapper(item, wrap));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @Description("Give an unwrapper to a player.")
    @AutoComplete("* *")
    public void onGiveUnwrapper(CommandSender sender, @Default("self") Player player, @Optional @Range(min = 1, max = 64) Integer amount) {
        if (!PermissionHelper.hasAnyPermission(sender, GIVE_UNWRAPPER_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        var item = plugin.getConfiguration().getUnwrapper().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalUnwrapper(item));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_UNWRAPPER);
    }

    @Subcommand("list")
    @Description("Shows all wraps and collections configured.")
    public void onList(CommandSender sender) {
        if (!PermissionHelper.hasAnyPermission(sender, LIST_PERMISSION)) {
            plugin.getMessageHandler().send(sender, Messages.NO_PERMISSION);
            return;
        }
        var handler = plugin.getMessageHandler();
        var set = new ArrayList<Component>();
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_HEADER)));
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS)));
        plugin.getConfiguration().getCollections().forEach((key, list) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", key)));
            list.forEach(entry -> set.add(
                    StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS_FORMAT), Placeholder.parsed("value", entry))));
        });
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS)));
        plugin.getWrappableItems().forEach((material, wraps) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", material)));
            wraps.getWraps().forEach((ignored, wrap) -> {
                var uuid = wrap.getUuid();
                var placeholders = List.of(Placeholder.parsed("value", uuid), Placeholder.parsed("permission", wrap.getPermission().orElse("None")),
                        Placeholder.parsed("modelid", String.valueOf(wrap.getModelId())),
                        Placeholder.parsed("player", sender instanceof Player player ? player.getName() : " "),
                        Placeholder.parsed("physical", String.valueOf(wrap.getPhysical().isPresent())),
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

    @Subcommand("help")
    @Description("Shows the help page.")
    public void onHelp(CommandSender sender, CommandHelp<String> helpEntries) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_HEADER);
        Supplier<Stream<String>> filteredEntries = () -> helpEntries.paginate(1, 100).stream().filter(string -> !string.equals(""));
        if (filteredEntries.get().findAny().isEmpty()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_NO_PERMISSION);
        } else {
            filteredEntries.get().forEach((line) -> StringUtil.send(sender, line));
        }
    }

}
