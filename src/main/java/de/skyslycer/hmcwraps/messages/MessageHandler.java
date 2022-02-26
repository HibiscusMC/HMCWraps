package de.skyslycer.hmcwraps.messages;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.StringUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.PropertyResourceBundle;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MessageHandler {

    private PropertyResourceBundle bundle;
    private PropertyResourceBundle fallback;

    private final HMCWraps plugin;

    public MessageHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    public boolean load(Path path) {
        try {
            fallback = new PropertyResourceBundle(HMCWraps.class.getClassLoader().getResource("messages.properties").openStream());
        } catch (IOException | NullPointerException exception) {
            plugin.getLogger().severe("An error occurred while trying to load the fallback messages (please report this to the developers):");
            exception.printStackTrace();
        }

        try {
            bundle = new PropertyResourceBundle(Files.newInputStream(path));
        } catch (IOException exception) {
            plugin.getLogger().severe("An error occurred while trying to load the messages (please report this to the developers):");
            exception.printStackTrace();
        }

        if (bundle == null && fallback == null) {
            plugin.getLogger().severe("""
                    =============================
                    Could not load any messages (please report this to the developers)! The plugin will shut down now.
                    =============================
                    """
            );
            return false;
        }
        return true;
    }

    public String get(Messages key) {
        if (bundle.containsKey(key.getKey())) {
            return bundle.getString(key.getKey());
        } else if (fallback.containsKey(key.getKey())){
            return fallback.getString(key.getKey());
        } else {
            return "Invalid key: " + key;
        }
    }

    public void send(CommandSender sender, Messages key, Single... placeholders) {
        StringUtil.send(sender, get(key), placeholders);
    }

}
