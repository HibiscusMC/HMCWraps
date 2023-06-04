package de.skyslycer.hmcwraps.serialization.debug;

import de.skyslycer.hmcwraps.serialization.Config;

public class DebugConfig extends Config implements Debuggable {

    public DebugConfig(Config config) {
        super(config.getUpdater(), config.getPermissions(), config.getPreview(), config.getFavorites(), config.getInventory(), config.getUnwrapper(),
                config.getPreservation(), null, null, config.getFilter());
    }

}
