package de.skyslycer.hmcwraps.actions;

import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.serialization.IWrap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

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
     * @param action      The action to push
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
     * Push all unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushUnwrap(IWrap wrap, Player player);

    /**
     * Push all wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushWrap(IWrap wrap, Player player);

    /**
     * Push all physical wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushPhysicalUnwrap(IWrap wrap, Player player);

    /**
     * Push all physical unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushPhysicalWrap(IWrap wrap, Player player);

    /**
     * Push all virtual wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushVirtualUnwrap(IWrap wrap, Player player);

    /**
     * Push all virtual unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushVirtualWrap(IWrap wrap, Player player);

    /**
     * Push all preview actions to all subscribers from the config.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    void pushPreview(IWrap wrap, Player player);

}
