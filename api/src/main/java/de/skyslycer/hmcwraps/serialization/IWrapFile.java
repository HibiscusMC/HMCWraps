package de.skyslycer.hmcwraps.serialization;

import java.util.Map;

public interface IWrapFile extends IToggleable {

    Map<String, IWrappableItem> getItems();

}
