package de.skyslycer.hmcwraps.serialization;

import java.util.Map;

public interface IWrappableItem {

    Map<String, ? extends IWrap> getWraps();

}
