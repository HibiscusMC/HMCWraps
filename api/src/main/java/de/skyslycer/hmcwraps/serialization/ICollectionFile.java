package de.skyslycer.hmcwraps.serialization;

import java.util.List;
import java.util.Map;

public interface ICollectionFile extends IToggleable {

    Map<String, List<String>> getCollections();

}
