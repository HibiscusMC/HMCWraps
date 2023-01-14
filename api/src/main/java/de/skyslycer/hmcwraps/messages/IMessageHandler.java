package de.skyslycer.hmcwraps.messages;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;

public interface IMessageHandler {

    /**
     * Load messages from a path.
     *
     * @param path The path to load from
     * @return If it was successful
     */
    boolean load(Path path);

    /**
     * Get a message based on its key.
     *
     * @param key The message key
     * @return The message
     */
    String get(Messages key);

    /**
     * Try to update the given .properties file by adding missing messages, which are present in the internal .properties file
     *
     * @param path The file to update
     */
    void update(Path path);

    /**
     * Send a message to a sender with replacing placeholders.
     *
     * @param sender       The receiver
     * @param key          The message key
     * @param placeholders The placeholders to replace
     */
    void send(CommandSender sender, Messages key, Single... placeholders);

}
