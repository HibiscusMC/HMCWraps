package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.inventory.IInventory;
import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import de.skyslycer.hmcwraps.serialization.preservation.IPreservationSettings;
import de.skyslycer.hmcwraps.serialization.preview.IPreviewSettings;
import de.skyslycer.hmcwraps.serialization.updater.IUpdaterSettings;

import java.util.List;
import java.util.Map;

public interface IConfig {

    IInventory getInventory();

    ISerializableItem getUnwrapper();

    Map<String, IWrappableItem> getItems();

    IPreviewSettings getPreview();

    IUpdaterSettings getUpdater();

    Map<String, List<String>> getCollections();

    IPermissionSettings getPermissions();

    IPreservationSettings getPreservation();

    IToggleable getFavorites();

}
