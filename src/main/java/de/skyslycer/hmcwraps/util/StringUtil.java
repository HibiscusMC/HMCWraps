package de.skyslycer.hmcwraps.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StringUtil {

    public static final MiniMessage miniMessage = MiniMessage.builder().build();

    public static Component parseComponent(String message, Single... placeholders) {
        String string = ChatColor.translateAlternateColorCodes('&', message);
        return Component.text().decoration(TextDecoration.ITALIC, false).append(miniMessage.deserialize(string, placeholders)).build();
    }

    public static Component parseComponent(CommandSender sender, String message, Single... placeholders) {
        String string = ChatColor.translateAlternateColorCodes('&', message);
        return Component.text().decoration(TextDecoration.ITALIC, false)
                .append(miniMessage.deserialize(replacePlaceholders(sender, string), placeholders)).build();
    }

    public static BaseComponent[] parse(CommandSender sender, String message, Single... placeholders) {
        return BungeeComponentSerializer.get().serialize(parseComponent(sender, message, placeholders));
    }

    public static void send(CommandSender sender, String message, Single... placeholders) {
        sender.spigot().sendMessage(parse(sender, message, placeholders));
    }

    private static String replacePlaceholders(CommandSender sender, String string) {
        if (sender instanceof Player player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

}
