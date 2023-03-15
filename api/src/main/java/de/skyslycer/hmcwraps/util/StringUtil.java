package de.skyslycer.hmcwraps.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class StringUtil {

    public static final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(StandardTags.defaults()).build();
    public static final LegacyComponentSerializer LEGACY_SERIALIZER_AMPERSAND = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors()
            .useUnusualXRepeatedCharacterHexFormat().build();
    public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('§').hexCharacter('#').hexColors()
            .useUnusualXRepeatedCharacterHexFormat().build();

    private static final Pattern SHORT_TIME_PATTERN = Pattern.compile("(\\d+)([dhms])+$");
    private static final Pattern PAPI_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    /**
     * Parse MiniMessage from a string and replace placeholders.
     *
     * @param message      The string to parse
     * @param placeholders The placeholders
     * @return The parsed component
     */
    public static Component parseComponent(String message, TagResolver... placeholders) {
        String string = legacyToMiniMessage(message);
        return Component.text().decoration(TextDecoration.ITALIC, false).append(MINI_MESSAGE.deserialize(string, TagResolver.resolver(placeholders)))
                .build();
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders.
     *
     * @param sender       The sender
     * @param message      The string to parse
     * @param placeholders The placeholders
     * @return The parsed component
     */
    public static Component parseComponent(CommandSender sender, String message, TagResolver... placeholders) {
        var list = new ArrayList<>(Arrays.asList(placeholders));
        list.add(papiTag(sender));
        return Component.text().decoration(TextDecoration.ITALIC, false)
                .append(MINI_MESSAGE.deserialize(replacePlaceholders(message), list.toArray(new TagResolver[0]))).build();
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders and return a Spigot friendly component.
     *
     * @param sender       The sender
     * @param message      The string to parse
     * @param placeholders The placeholders
     * @return The BaseComponent array used in Spigot
     */
    public static BaseComponent[] parse(CommandSender sender, String message, TagResolver... placeholders) {
        return BungeeComponentSerializer.get().serialize(parseComponent(sender, message, placeholders));
    }

    /**
     * Parse MiniMessage from a string, replace placeholders and replace and PlaceholderAPI placeholders and send it to the sender.
     *
     * @param sender       The sender
     * @param message      The string to parse
     * @param placeholders The placeholders
     */
    public static void send(CommandSender sender, String message, TagResolver... placeholders) {
        sender.spigot().sendMessage(parse(sender, message, placeholders));
    }

    public static void sendComponent(CommandSender sender, Component component) {
        sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(component));
    }

    /**
     * Replace all PlaceholderAPI placeholders with MiniMessage tags in a string.
     *
     * @param string The string
     * @return A replaced string
     */
    public static String replacePlaceholders(String string) {
        return PAPI_PLACEHOLDER_PATTERN.matcher(string).replaceAll(matchResult -> "<papi:" + matchResult.group(1) + ">");
    }

    /**
     * Replace all PlaceholderAPI placeholders in a string.
     *
     * @param sender The sender
     * @param string The string
     * @return A replaced string
     */
    public static String replacePlaceholders(CommandSender sender, String string) {
        if (sender instanceof Player player && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

    /**
     * Parse a color from a string.
     * Formats: #RRGGBB; R,G,B
     *
     * @param color The string
     * @return The color, if the string can't be parsed, null is returned
     */
    @Nullable
    public static Color colorFromString(@Nullable String color) {
        if (color == null) {
            return null;
        }
        try {
            var decodedColor = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
            return Color.fromRGB(decodedColor.getRed(), decodedColor.getGreen(), decodedColor.getBlue());
        } catch (NumberFormatException invalidHex) {
            try {
                var rgbValues = Arrays.stream(color.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
                return Color.fromRGB(rgbValues[0], rgbValues[1], rgbValues[2]);
            } catch (Exception invalidRgb) {
                return null;
            }
        }
    }

    /**
     * Convert legacy color codes (&c) to MiniMessage (<red>).
     * If the string contains legacy color codes, other MiniMessage tags won't work.
     *
     * @param legacy The string containing legacy color codes
     * @return The string with MiniMessage color codes
     */
    public static String legacyToMiniMessage(String legacy) {
        try {
            MINI_MESSAGE.deserialize(ChatColor.translateAlternateColorCodes('&', legacy));
            return legacy;
        } catch (ParsingException exception) {
            return MINI_MESSAGE.serialize(LEGACY_SERIALIZER_AMPERSAND.deserialize(legacy));
        }
    }

    /**
     * Convert short time to seconds. (9h, 20s, 4m)
     *
     * @param shortTime      The short time string
     * @param minSeconds     The minimum amount of seconds
     * @param defaultSeconds The default seconds when it can't be parsed
     * @return The amount of seconds
     */
    public static long shortTimeToSeconds(String shortTime, long minSeconds, long defaultSeconds) {
        var matcher = SHORT_TIME_PATTERN.matcher(shortTime);
        if (!matcher.matches()) {
            return defaultSeconds;
        }
        var amount = Long.parseLong(matcher.group(1));
        return switch (matcher.group(2)) {
            case "d" -> Math.max(amount * 86400, minSeconds);
            case "h" -> Math.max(amount * 3600, minSeconds);
            case "m" -> Math.max(amount * 60, minSeconds);
            case "s" -> Math.max(amount, minSeconds);
            default -> defaultSeconds;
        };
    }

    private static TagResolver papiTag(CommandSender sender) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            if (!(sender instanceof Player player) || !Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                return Tag.selfClosingInserting(Component.empty());
            }
            var inserting = argumentQueue.hasNext() && argumentQueue.peek().value().equals("inserting");
            var placeholder = argumentQueue.popOr("The PlaceholderAPI tag requires an argument!").value();
            var parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + placeholder + '%');
            var componentPlaceholder = LEGACY_SERIALIZER.deserialize(parsedPlaceholder);
            return inserting ? Tag.inserting(componentPlaceholder) : Tag.selfClosingInserting(componentPlaceholder);
        });
    }

}
