package de.skyslycer.hmcwraps.actions;

import de.skyslycer.hmcwraps.actions.information.ActionInformation;

public interface ActionMethod {

    /**
     * This method is called when this action is pushed.
     *
     * @param information The pushed information
     */
    void execute(ActionInformation information);

}
