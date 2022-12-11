package de.skyslycer.hmcwraps.actions;

import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.serialization.IWrap;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;

public interface IActionHandler {

    /**
     * Subscribe a method to an action.
     *
     * @param action The action to subscribe to
     * @param method The method to subscribe
     */
    void subscribe(Action action, ActionMethod method);

    /**
     * Push an action to all subscribers.
     *
     * @param action The action to push
     * @param information The action information
     */
    void push(Action action, ActionInformation information);

    /**
     * Push an action to all subscribers from the config.
     *
     * @param actionTypes Config entry with all actions that should be pushed
     * @param information The action information
     */
    void pushFromConfig(HashMap<String, List<String>> actionTypes, ActionInformation information);

    /**
     * Push the unwrap action to all subscribers.
     *
     * @param wrap The wrap
     * @param player the player
     */
    void pushUnwrap(IWrap wrap, Player player);

    /**
     * Push the wrap action to all subscribers.
     *
     * @param wrap The wrap
     * @param player the player
     */
    void pushWrap(IWrap wrap, Player player);

    /**
     * Push the preview action to all subscribers from the config.
     *
     * @param wrap The wrap
     * @param player the player
     */
    void pushPreview(IWrap wrap, Player player);

}
