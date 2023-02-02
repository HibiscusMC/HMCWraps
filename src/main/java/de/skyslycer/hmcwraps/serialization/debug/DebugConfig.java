package de.skyslycer.hmcwraps.serialization.debug;

import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import de.skyslycer.hmcwraps.serialization.permission.PermissionSettings;
import de.skyslycer.hmcwraps.serialization.preservation.PreservationSettings;
import de.skyslycer.hmcwraps.serialization.preview.PreviewSettings;
import de.skyslycer.hmcwraps.serialization.updater.UpdaterSettings;

public class DebugConfig extends Config implements Debuggable {

    public DebugConfig(Config config) {
        super((UpdaterSettings) config.getUpdater(), (PermissionSettings) config.getPermissions(), (PreviewSettings) config.getPreview(), (Toggleable) config.getFavorites(),
                (Inventory) config.getInventory(), (SerializableItem) config.getUnwrapper(), (PreservationSettings) config.getPreservation(), null, null);
    }

}
