package de.skyslycer.hmcwraps.serialization;

import com.comphenix.protocol.injector.PacketConstructor.Unwrapper;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import java.util.Map;

public class Config {

    private Inventory inventory;
    private SerializableItem unwrapper;
    private Map<String, WrappableItem> items;

}
