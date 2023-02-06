package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.filter.FilterSettings;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.permission.PermissionSettings;
import de.skyslycer.hmcwraps.serialization.preservation.PreservationSettings;
import de.skyslycer.hmcwraps.serialization.preview.PreviewSettings;
import de.skyslycer.hmcwraps.serialization.updater.UpdaterSettings;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Config {

    private UpdaterSettings updater;
    @Setting("permission-settings")
    private PermissionSettings permissions;
    private PreviewSettings preview;
    private Toggleable favorites;
    private FilterSettings filter;
    private Inventory inventory;
    private SerializableItem unwrapper;
    private PreservationSettings preservation;
    private Map<String, WrappableItem> items = new HashMap<>();
    private Map<String, List<String>> collections = new HashMap<>();

    public Config(UpdaterSettings updater, PermissionSettings permissions, PreviewSettings preview, Toggleable favorites,
                  Inventory inventory, SerializableItem unwrapper, PreservationSettings preservation, Map<String, WrappableItem> items,
                  Map<String, List<String>> collections, FilterSettings filter) {
        this.updater = updater;
        this.permissions = permissions;
        this.preview = preview;
        this.favorites = favorites;
        this.inventory = inventory;
        this.unwrapper = unwrapper;
        this.preservation = preservation;
        this.items = items;
        this.collections = collections;
        this.filter = filter;
    }

    public Config() { }

    public Inventory getInventory() {
        return inventory;
    }

    public SerializableItem getUnwrapper() {
        return unwrapper;
    }

    public Map<String, WrappableItem> getItems() {
        return items;
    }

    public PreviewSettings getPreview() {
        return preview;
    }

    public UpdaterSettings getUpdater() {
        return updater;
    }

    public Map<String, List<String>> getCollections() {
        return collections;
    }

    public PermissionSettings getPermissions() {
        return permissions;
    }

    public PreservationSettings getPreservation() {
        return preservation;
    }

    public Toggleable getFavorites() {
        return favorites;
    }

    public FilterSettings getFilter() {
        return filter;
    }

}
