package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.ColorUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WrapCreateCommand {

    private static final String CREATE_PERMISSION = "hmcwraps.commands.create";

    private final HMCWrapsPlugin plugin;

    public WrapCreateCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("wraps create")
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

}
