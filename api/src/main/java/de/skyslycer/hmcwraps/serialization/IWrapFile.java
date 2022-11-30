package de.skyslycer.hmcwraps.serialization;

import java.util.Map;

public interface IWrapFile {

    Map<String, IWrappableItem> getItems();

    boolean isEnabled();
}
