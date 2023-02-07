package de.skyslycer.hmcwraps;

import de.skyslycer.hmcwraps.actions.ActionHandler;
import de.skyslycer.hmcwraps.itemhook.HookAccessor;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.pool.ObjectPool;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.files.CollectionFile;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.storage.Storage;
import de.skyslycer.hmcwraps.wrap.CollectionHelper;
import de.skyslycer.hmcwraps.wrap.Wrapper;
import de.skyslycer.hmcwraps.wrap.WrapsLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface HMCWraps extends Plugin {

    Path PLUGIN_PATH = Path.of("plugins", "HMCWraps");
    Path CONFIG_PATH = PLUGIN_PATH.resolve("config.yml");
    Path WRAP_FILES_PATH = PLUGIN_PATH.resolve("wraps");
    Path MESSAGES_PATH = PLUGIN_PATH.resolve("messages.properties");
    Path CONVERT_PATH = PLUGIN_PATH.resolve("convert");
    Path COLLECTION_FILES_PATH = PLUGIN_PATH.resolve("collections");

    /**
     * Load the plugin. Used for reload.
     *
     * @return If the plugin was able to load
     */
    boolean load();

    /**
     * Unload the plugin. Used for reload.
     */
    void unload();

    /**
     * Log an error that stands out from other system messages.
     *
     * @param message The message to display
     */
    void logSevere(String message);

    /**
     * Get the config.
     *
     * @return The config
     */
    Config getConfiguration();

    /**
     * Get the message handler.
     *
     * @return The message handler
     */
    MessageHandler getMessageHandler();

    /**
     * Get the wrapper.
     *
     * @return The wrapper
     */
    Wrapper getWrapper();

    /**
     * Get the preview manager.
     *
     * @return The preview manager
     */
    PreviewManager getPreviewManager();

    /**
     * Get the collection helper.
     *
     * @return The collection helper.
     */
    CollectionHelper getCollectionHelper();

    /**
     * Get the action handler.
     *
     * @return The action handler
     */
    ActionHandler getActionHandler();

    /**
     * Get the message pool.
     *
     * @return The message pool
     */
    ObjectPool<UUID, Component> getMessagePool();

    /**
     * Get the storage storing the state of the filter.
     *
     * @return The filter storage
     */
    Storage<Player, Boolean> getFilterStorage();

    /**
     * Get the storage storing the favorite wraps of a player.
     *
     * @return The filter storage
     */
    Storage<Player, List<Wrap>> getFavoriteWrapStorage();

    /**
     * Get the wraps loader.
     *
     * @return The wraps loader
     */
    WrapsLoader getWrapsLoader();

    /**
     * Get the hook accessor.
     *
     * @return The hook accessor
     */
    HookAccessor getHookAccessor();

}
