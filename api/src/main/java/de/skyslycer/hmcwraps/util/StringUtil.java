package de.skyslycer.hmcwraps.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StringUtil {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(StandardTags.defaults()).build();

    /**
     * Parse MiniMessage from a string and replace placeholders.
     *
     * @param message The string to parse
     * @param placeholders The placeholders
     * @return The parsed component
     */
    public static Component parseComponent(String message, TagResolver... placeholders) {
        String string = ChatColor.translateAlternateColorCodes('&', message);
        return Component.text().decoration(TextDecoration.ITALIC, false).append(MINI_MESSAGE.deserialize(string, TagResolver.resolver(placeholders)))
                .build();
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders.
     *
     * @param sender The sender
     * @param message The string to parse
     * @param placeholders The placeholders
     * @return The parsed component
     */
    public static Component parseComponent(CommandSender sender, String message, TagResolver... placeholders) {
        String string = ChatColor.translateAlternateColorCodes('&', message);
        return Component.text().decoration(TextDecoration.ITALIC, false)
                .append(MINI_MESSAGE.deserialize(replacePlaceholders(sender, string), placeholders)).build();
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders and return a Spigot friendly component.
     *
     * @param sender The sender
     * @param message The string to parse
     * @param placeholders The placeholders
     * @return The BaseComponent array used in Spigot
     */
    public static BaseComponent[] parse(CommandSender sender, String message, TagResolver... placeholders) {
        return BungeeComponentSerializer.get().serialize(parseComponent(sender, message, placeholders));
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders and send it to the sender.
     *
     * @param sender The sender
     * @param message The string to parse
     * @param placeholders The placeholders
     */
    public static void send(CommandSender sender, String message, TagResolver... placeholders) {
        sender.spigot().sendMessage(parse(sender, message, placeholders));
    }

    /**
     * Replace all PlaceholderAPI placeholders in a string.
     *
     * @param sender The sender
     * @param string The string
     * @return A replaced string
     */
    private static String replacePlaceholders(CommandSender sender, String string) {
        if (sender instanceof Player player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

}
