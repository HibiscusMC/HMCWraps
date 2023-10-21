package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.ColorUtil;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.lone.itemsadder.api.CustomStack;
import dev.triumphteam.gui.guis.BaseGui;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Command("wraps")
public class WrapCommand {

    public static final String RELOAD_PERMISSION = "hmcwraps.commands.reload";
    public static final String CONVERT_PERMISSION = "hmcwraps.commands.convert";
    public static final String WRAP_PERMISSION = "hmcwraps.commands.wrap";
    public static final String UNWRAP_PERMISSION = "hmcwraps.commands.unwrap";
    public static final String GIVE_WRAPPER_PERMISSION = "hmcwraps.commands.give.wrapper";
    public static final String GIVE_UNWRAPPER_PERMISSION = "hmcwraps.commands.give.unwrapper";
    public static final String PREVIEW_PERMISSION = "hmcwraps.commands.preview";
    public static final String LIST_PERMISSION = "hmcwraps.commands.list";
    public static final String CREATE_PERMISSION = "hmcwraps.commands.create";
    public static final String WRAPS_PERMISSION = "hmcwraps.wraps";

    private final Set<String> confirmingPlayers = new HashSet<>();

    private final HMCWrapsPlugin plugin;

    public WrapCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        if (plugin.getConfiguration().getPermissions().isInventoryPermission() && !player.hasPermission(WRAPS_PERMISSION)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(player, Messages.NO_ITEM);
            return;
        }
        var type = item.getType();
        if (plugin.getWrapper().getWrap(item) != null && !plugin.getWrapper().getOriginalData(item).material().isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getOriginalData(item).material());
        }
        if (plugin.getCollectionHelper().getItems(type).isEmpty()) {
            plugin.getMessageHandler().send(player, Messages.NO_WRAPS);
            return;
        }
        var slot = player.getInventory().getHeldItemSlot();
        GuiBuilder.open(plugin, player, player.getInventory().getItem(slot), slot);
    }

    @Subcommand("reload")
    @CommandPermission(RELOAD_PERMISSION)
    @Description("Reload configuration and messages.")
    public void onReload(CommandSender sender) {
        var current = System.nanoTime();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof BaseGui) {
                player.closeInventory();
            }
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            plugin.unload();
            plugin.load();
            plugin.getMessageHandler().send(sender, Messages.COMMAND_RELOAD,
                    Placeholder.parsed("time", String.format("%.2f", (System.nanoTime() - current) / 1_000_000.0)),
                    Placeholder.parsed("wraps", String.valueOf(plugin.getWrapsLoader().getWraps().size())),
                    Placeholder.parsed("collections", String.valueOf(plugin.getWrapsLoader().getCollections().size())));
        }, 0L);
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
    @Description("Wrap the item a player is holding in their main hand.")
    @CommandPermission(WRAP_PERMISSION)
    @AutoComplete("@wraps @players @actions")
    public void onWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Optional String actions) {
        if (wrap == null) {
            return;
        }
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        for (WrappableItem wrappableItem : plugin.getCollectionHelper().getItems(item.getType())) {
            if (wrappableItem.getWraps().containsValue(wrap)) {
                item = plugin.getWrapper().setWrap(wrap, item, false, player, true);
                item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
                player.getInventory().setItemInMainHand(item);
                if (actions == null || !actions.equals("-actions")) {
                    plugin.getActionHandler().pushWrap(wrap, player);
                    plugin.getActionHandler().pushVirtualWrap(wrap, player);
                }
                plugin.getMessageHandler().send(sender, Messages.COMMAND_WRAP_WRAPPED);
                return;
            }
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
    }

    @Subcommand("unwrap")
    @Description("Unwrap the item a player is holding in their main hand.")
    @CommandPermission(UNWRAP_PERMISSION)
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
        item = plugin.getWrapper().removeWrap(item, player, true);
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
    @AutoComplete("@physicalWraps @players *")
    public void onGiveWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Range(min = 1, max = 64) @Optional Integer amount) {
        if (wrap == null) {
            return;
        }
        if (wrap.getPhysical() == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return;
        }
        var item = wrap.getPhysical().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalWrapper(item, wrap));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @Description("Give an unwrapper to a player.")
    @CommandPermission(GIVE_UNWRAPPER_PERMISSION)
    @AutoComplete("@players *")
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
        plugin.getWrapsLoader().getWrappableItems().forEach((material, wraps) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", material)));
            wraps.getWraps().forEach((ignored, wrap) -> {
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

    @Subcommand("create")
    @Description("Create a wrap from an item in hand!")
    @CommandPermission(CREATE_PERMISSION)
    public void onCreate(Player player, String file, String uuid, @Optional String collection) {
        var path = HMCWraps.COMMAND_PATH.resolve(file + (file.endsWith(".yml") ? "" : ".yml"));
        var item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            plugin.getMessageHandler().send(player, Messages.COMMAND_NEED_ITEM);
            return;
        }
        var checkedCollection = collection == null ? item.getType().toString() : collection;
        var newWrap = createWrapFromItem(item, uuid);

        WrapFile wrapFile;
        if (Files.exists(path)) {
            wrapFile = updateExistingWrapFile(player, path, item, newWrap, checkedCollection);
        } else {
            wrapFile = createNewWrapFile(item, newWrap, checkedCollection);
        }

        saveWrapFile(path, wrapFile, player);
    }

    private Wrap createWrapFromItem(ItemStack item, String uuid) {
        var meta = item.getItemMeta();
        String color = null;
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            color = ColorUtil.colorToHex(leatherArmorMeta.getColor());
        }
        String id = getHookId(item);
        String name = StringUtil.legacyToMiniMessage(meta.hasDisplayName() ? meta.getDisplayName().replace("§", "&") : StringUtil.convertToTitleCase(item.getType().name()));
        List<String> lore = extractLore(meta);
        int amount = item.getAmount();
        List<String> flags = meta.getItemFlags().stream().map(Enum::name).toList();
        Map<String, Integer> enchantments = extractEnchantments(item);

        return new Wrap(id, name, lore, null, uuid, color, amount == 1 ? null : amount, flags.isEmpty() ? null : flags, enchantments);
    }

    private List<String> extractLore(ItemMeta meta) {
        List<String> lore = new ArrayList<>();
        if (meta.hasLore()) {
            meta.getLore().forEach(line -> lore.add(StringUtil.legacyToMiniMessage(line.replace("§", "&"))));
        } else {
            return null;
        }
        return lore;
    }

    private Map<String, Integer> extractEnchantments(ItemStack item) {
        Map<String, Integer> enchantments = new HashMap<>();
        item.getEnchantments().forEach((enchantment, integer) -> enchantments.put(enchantment.getKey().getKey().toUpperCase(), integer));
        if (enchantments.isEmpty()) {
            return null;
        }
        return enchantments;
    }

    private WrapFile updateExistingWrapFile(Player player, Path path, ItemStack item, Wrap newWrap, String collection) {
        try {
            var existingFile = YamlConfigurationLoader.builder()
                    .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                    .path(path)
                    .build().load().get(WrapFile.class);
            var items = existingFile.getItems();
            if (items.containsKey(collection)) {
                var wrappableItem = items.get(collection);
                wrappableItem.putWrap(String.valueOf(getUnusedId(wrappableItem.getWraps())), newWrap);
                items.put(collection, wrappableItem);
            } else {
                var wraps = new HashMap<String, Wrap>();
                wraps.put("1", newWrap);
                var wrappableItem = new WrappableItem(wraps);
                items.put(collection, wrappableItem);
            }
            return new WrapFile(items, true);
        } catch (ConfigurateException exception) {
            handleException(player, exception, "loading the existing wrap file");
            return null;
        }
    }

    private WrapFile createNewWrapFile(ItemStack item, Wrap newWrap, String collection) {
        var wraps = new HashMap<String, Wrap>();
        wraps.put("1", newWrap);
        var wrappableItem = new WrappableItem(wraps);
        var items = new HashMap<String, WrappableItem>();
        items.put(collection, wrappableItem);
        return new WrapFile(items, true);
    }

    private void saveWrapFile(Path path, WrapFile wrapFile, Player player) {
        try {
            YamlConfigurationLoader.builder()
                    .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                    .path(path).indent(2)
                    .nodeStyle(NodeStyle.BLOCK)
                    .build().save(BasicConfigurationNode.factory().createNode().set(wrapFile));
            plugin.getMessageHandler().send(player, Messages.COMMAND_CREATE_SUCCESS, Placeholder.parsed("path", "plugins/HMCWraps/wraps/command/" + path.getFileName().toString()));
        } catch (ConfigurateException exception) {
            handleException(player, exception, "saving the wrap file");
        }
    }

    private void handleException(Player player, ConfigurateException exception, String action) {
        plugin.getMessageHandler().send(player, Messages.COMMAND_CREATE_FAILED);
        plugin.getLogger().severe("An error occurred while " + action + "! Please report this to the developers.");
        exception.printStackTrace();
    }

    private int getUnusedId(Map<String, Wrap> wraps) {
        int id = wraps.size();
        while (wraps.containsKey(String.valueOf(id))) {
            id++;
        }
        return id;
    }

    private String getHookId(ItemStack item) {
        if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder") && CustomStack.byItemStack(item) != null) {
            return "itemsadder:" + CustomStack.byItemStack(item).getNamespacedID();
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Oraxen") && OraxenItems.getIdByItem(item) != null) {
            return "oraxen:" + OraxenItems.getIdByItem(item);
        }
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs") && MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item) != null) {
            return "mythic:" + MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
        }
        var meta = item.getItemMeta();
        return String.valueOf(meta.hasCustomModelData() ? meta.getCustomModelData() : -1);
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
