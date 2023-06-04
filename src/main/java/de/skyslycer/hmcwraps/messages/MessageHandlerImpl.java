package de.skyslycer.hmcwraps.messages;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.StringUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.PropertyResourceBundle;

public class MessageHandlerImpl implements MessageHandler {

    private final HMCWrapsPlugin plugin;
    private PropertyResourceBundle bundle;
    private PropertyResourceBundle fallback;

    public MessageHandlerImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean load(Path path) {
        try {
            fallback = new PropertyResourceBundle(HMCWrapsPlugin.class.getClassLoader().getResource("messages.properties").openStream());
        } catch (IOException | NullPointerException exception) {
            plugin.logSevere("An error occurred while trying to load the fallback messages (please report this to the developers):", exception);
        }

        try {
            bundle = new PropertyResourceBundle(Files.newInputStream(path));
        } catch (IOException exception) {
            plugin.logSevere("An error occurred while trying to load the messages (please report this to the developers):", exception);
        }

        if (bundle == null && fallback == null) {
            plugin.logSevere("Could not load any messages (please report this to the developers)! The plugin will shut down now.");
            return false;
        }
        return true;
    }

    @Override
    public String get(Messages key) {
        if (bundle.containsKey(key.getKey())) {
            return bundle.getString(key.getKey());
        } else if (fallback.containsKey(key.getKey())) {
            return fallback.getString(key.getKey());
        } else {
            return "Invalid key: " + key;
        }
    }

    @Override
    public void update(Path path) {
        try {
            var stream = HMCWrapsPlugin.class.getClassLoader().getResource("messages.properties").openStream();
            var lines = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines();
            var checkLines = Files.readAllLines(path);
            lines.forEach(line -> {
                var split = line.split("=");
                if (split.length > 1 && checkLines.stream().filter(it -> it.startsWith(split[0])).findFirst().isEmpty()) {
                    try {
                        Files.writeString(path, '\n' + line, StandardOpenOption.APPEND);
                    } catch (IOException exception) {
                        plugin.logSevere("Could not append the following line: \n" + line, exception);
                    }
                }
            });
        } catch (Exception exception) {
            plugin.logSevere("Could not load the message files to update them!", exception);
        }
    }

    @Override
    public void send(CommandSender sender, Messages key, Single... placeholders) {
        var message = StringUtil.parseComponent(sender, get(key), placeholders);
        if (sender instanceof Player player) {
            plugin.getMessagePool().execute(player.getUniqueId(), message, () -> StringUtil.sendComponent(sender, message));
            return;
        }
        StringUtil.sendComponent(sender, message);
    }

}
