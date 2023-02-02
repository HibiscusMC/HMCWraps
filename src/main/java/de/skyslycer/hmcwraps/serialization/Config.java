package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.filter.FilterSettings;
import de.skyslycer.hmcwraps.serialization.filter.IFilterSettings;
import de.skyslycer.hmcwraps.serialization.inventory.IInventory;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.permission.PermissionSettings;
import de.skyslycer.hmcwraps.serialization.preservation.IPreservationSettings;
import de.skyslycer.hmcwraps.serialization.preservation.PreservationSettings;
import de.skyslycer.hmcwraps.serialization.preview.IPreviewSettings;
import de.skyslycer.hmcwraps.serialization.preview.PreviewSettings;
import de.skyslycer.hmcwraps.serialization.updater.IUpdaterSettings;
import de.skyslycer.hmcwraps.serialization.updater.UpdaterSettings;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
public class Config implements IConfig {

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

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public ISerializableItem getUnwrapper() {
        return unwrapper;
    }

    @Override
    public Map<String, IWrappableItem> getItems() {
        return new HashMap<>(items);
    }

    @Override
    public IPreviewSettings getPreview() {
        return preview;
    }

    @Override
    public IUpdaterSettings getUpdater() {
        return updater;
    }

    @Override
    public Map<String, List<String>> getCollections() {
        return collections;
    }

    @Override
    public IPermissionSettings getPermissions() {
        return permissions;
    }

    @Override
    public IPreservationSettings getPreservation() {
        return preservation;
    }

    @Override
    public IToggleable getFavorites() {
        return favorites;
    }

    @Override
    public IFilterSettings getFilter() {
        return filter;
    }

}
