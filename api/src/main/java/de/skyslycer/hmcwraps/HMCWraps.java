package de.skyslycer.hmcwraps;

import de.skyslycer.hmcwraps.actions.ActionHandler;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.pool.ObjectPool;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.files.CollectionFile;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.storage.Storage;
import de.skyslycer.hmcwraps.wrap.ICollectionHelper;
import de.skyslycer.hmcwraps.wrap.IWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
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
     * Get an item stack based on the input.
     *
     * @param id The input
     * @return The item stack
     */
    @Nullable
    ItemStack getItemFromHook(String id);

    /**
     * Get the model id corresponding to the input.
     *
     * @param id The input
     * @return The model id, may return -1 when none is available
     */
    int getModelIdFromHook(String id);

    /**
     * Log an error that stands out from other system messages.
     *
     * @param message The message to display
     */
    void logSevere(String message);

    /**
     * Get the amount of wraps currently configured.
     *
     * @return The amount of wraps
     */
    int getWrapAmount();

    /**
     * Get the config.
     *
     * @return The config
     */
    @NotNull Config getConfiguration();

    /**
     * Get the message handler.
     *
     * @return The message handler
     */
    @NotNull MessageHandler getMessageHandler();

    /**
     * All wraps currently configured.
     *
     * @return All wraps
     */
    @NotNull Map<String, Wrap> getWraps();

    /**
     * All wrap files currently loaded.
     * NOTE: This also contains disabled wrap files, you have to filter for enabled ones yourself.
     *
     * @return All currently loaded wrap files
     */
    @NotNull Set<WrapFile> getWrapFiles();

    /**
     * All currently loaded wrappable items.
     * This map contains all wraps that can be applied to a particular material/collection.
     *
     * @return All currently loaded wrappable items
     */
    @NotNull Map<String, WrappableItem> getWrappableItems();

    /**
     * Get the wrapper.
     *
     * @return The wrapper
     */
    @NotNull IWrapper getWrapper();

    /**
     * Get the preview manager.
     *
     * @return The preview manager
     */
    @NotNull PreviewManager getPreviewManager();

    /**
     * Get the collection helper.
     *
     * @return The collection helper.
     */
    @NotNull ICollectionHelper getCollectionHelper();

    /**
     * Get the action handler.
     *
     * @return The action handler
     */
    @NotNull ActionHandler getActionHandler();

    /**
     * Get all currently loaded collections.
     *
     * @return All currently loaded collections
     */
    @NotNull Map<String, List<String>> getCollections();

    /**
     * Get all currently loaded collection files.
     *
     * @return All collection files
     */
    @NotNull Set<CollectionFile> getCollectionFiles();

    /**
     * Get the message pool.
     *
     * @return The message pool
     */
    ObjectPool<UUID, Component> getMessagePool();

    Storage<Player, Boolean> getFilterStorage();

    Storage<Player, List<Wrap>> getFavoriteWrapStorage();

}
