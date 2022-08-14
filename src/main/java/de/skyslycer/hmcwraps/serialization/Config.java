package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.inventory.IInventory;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.preview.IPreviewSettings;
import de.skyslycer.hmcwraps.serialization.preview.PreviewSettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Config implements IConfig {

    private boolean openShortcut;
    private PermissionSettings permissionSettings;
    private PreviewSettings preview;
    private Inventory inventory;
    private SerializableItem unwrapper;
    private Map<String, WrappableItem> items = new HashMap<>();
    private Map<String, List<String>> collections = new HashMap<>();

    @Override
    public boolean isOpenShortcut() {
        return openShortcut;
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public ISerializableItem getUnwrapper() {
        return unwrapper;
    }

    @Override
    public Map<String, ? extends IWrappableItem> getItems() {
        return items;
    }

    @Override
    public IPreviewSettings getPreview() {
        return preview;
    }

    @Override
    public Map<String, List<String>> getCollections() {
        return collections;
    }

    @Override
    public IPermissionSettings getPermissionSettings() {
        return permissionSettings;
    }
}
