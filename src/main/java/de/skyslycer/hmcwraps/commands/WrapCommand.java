package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

@Command("wraps")
public class WrapCommand {

    private final HMCWraps plugin;

    public WrapCommand(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.getHandler().send(player, Messages.NO_ITEM);
            return;
        }
        if (plugin.getCollection().getItems(item.getType()).isEmpty()) {
            plugin.getHandler().send(player, Messages.NO_WRAPS);
            return;
        }
        GuiBuilder.open(plugin, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
    }

    @Subcommand("reload")
    @CommandPermission("hmcwraps.admin")
    @Description("Reload configuration and messages.")
    public void onReload(CommandSender sender) {
        plugin.unload();
        plugin.load();
        plugin.getHandler().send(sender, Messages.COMMAND_RELOAD);
    }

    @Subcommand("set")
    @CommandPermission("hmcwraps.admin")
    @Description("Wrap the item a player is holding in his main hand.")
    @AutoComplete("* @wraps")
    public void onSet(CommandSender sender, Player player, Wrap wrap) {
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType() == Material.AIR) {
            plugin.getHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        for (IWrappableItem wrappableItem : plugin.getCollection().getItems(item.getType())) {
            if (wrappableItem.getWraps().containsValue(wrap)) {
                item = plugin.getWrapper().setWrap(wrap.getModelId(), wrap.getUuid(), item, false, player, true);
                item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
                player.getInventory().setItemInMainHand(item);
                plugin.getHandler().send(sender, Messages.COMMAND_WRAP_APPLIED);
                return;
            }
        }
        plugin.getHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
    }

    @Subcommand("preview")
    @CommandPermission("hmcwraps.admin")
    @Description("Preview a wrap for the specified player.")
    @AutoComplete("* @wraps")
    public void onPreview(CommandSender sender, Player player, Wrap wrap) {
        var currentCollection = "";
        var itemMaterial = Material.AIR;
        for (Map.Entry<String, ? extends IWrappableItem> entry : plugin.getConfiguration().getItems().entrySet()) {
            currentCollection = entry.getKey();
            if (entry.getValue().getWraps().containsValue(wrap)) {
                break;
            }
        }
        if (currentCollection.equals("")) {
            plugin.getHandler().send(sender, Messages.COMMAND_INVALID_WRAP);
            return;
        }

        if (Material.getMaterial(currentCollection) != null) {
            itemMaterial = Material.getMaterial(currentCollection);
        } else if (plugin.getCollection().getMaterials(currentCollection).stream().findFirst().isPresent()) {
            itemMaterial = plugin.getCollection().getMaterials(currentCollection).stream().findFirst().get();
        } else {
            plugin.getHandler().send(sender, Messages.COMMAND_NO_MATCHING_ITEM);
            return;
        }
        plugin.getPreviewManager().create(player, ItemBuilder.from(itemMaterial).model(wrap.getModelId()).build(), null);
        plugin.getHandler().send(sender, Messages.COMMAND_PREVIEW_CREATED);
    }

    @Subcommand("give wrapper")
    @CommandPermission("hmcwraps.admin")
    @Description("Give a wrapper to a player.")
    @AutoComplete("* @physicalWraps *")
    public void onGiveWrap(CommandSender sender, Player player, Wrap wrap, @Range(min = 1, max = 64) @Optional Integer amount) {
        if (wrap.getPhysical().isEmpty()) {
            plugin.getHandler().send(sender, Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return;
        }
        var item = wrap.getPhysical().get().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setWrapper(item, wrap.getUuid()));
        plugin.getHandler().send(sender, Messages.COMMAND_GIVEN_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @CommandPermission("hmcwraps.admin")
    @Description("Give an unwrapper to a player.")
    public void onGiveUnwrapper(CommandSender sender, Player player, @Optional @Range(min = 1, max = 64) Integer amount) {
        var item = plugin.getConfiguration().getUnwrapper().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setUnwrapper(item));
        plugin.getHandler().send(sender, Messages.COMMAND_GIVEN_UNWRAPPER);
    }

    @Subcommand("help")
    @Description("Shows the help page.")
    public void onHelp(CommandSender sender, CommandHelp<String> helpEntries) {
        plugin.getHandler().send(sender, Messages.COMMAND_HELP_HEADER);
        Supplier<Stream<String>> filteredEntries = () -> helpEntries.paginate(1, 100).stream().filter(string -> !string.equals(""));
        if (filteredEntries.get().findAny().isEmpty()) {
            plugin.getHandler().send(sender, Messages.COMMAND_HELP_NO_PERMISSION);
        } else {
            filteredEntries.get().forEach((line) -> StringUtil.send(sender, line));
        }
    }

}
