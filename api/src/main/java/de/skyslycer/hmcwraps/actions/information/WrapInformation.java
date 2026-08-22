package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;

public interface WrapInformation extends ActionInformation {

    Wrap getWrap();

    void setWrap(Wrap wrap);

}
