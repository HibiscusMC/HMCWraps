package de.skyslycer.hmcwraps.actions;

import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.actions.information.WrapActionInformation;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ActionHandler {

    private final HashMap<Action, ActionMethod> actions = new HashMap<>();

    /**
     * Subscribe a method to an action.
     *
     * @param action The action to subscribe to
     * @param method The method to subscribe
     */
    public void subscribe(Action action, ActionMethod method) {
        actions.put(action, method);
    }

    /**
     * Push an action to all subscribers.
     *
     * @param action      The action to push
     * @param information The action information
     */
    public void push(Action action, ActionInformation information) {
        if (!actions.containsKey(action)) {
            Bukkit.getLogger().warning("Action " + action.name() + " is not registered! This is a bug! Please report this.");
            return;
        }
        actions.get(action).execute(information);
    }

    /**
     * Push an action to all subscribers from the config.
     *
     * @param actionTypes Config entry with all actions that should be pushed
     * @param information The action information
     */
    public void pushFromConfig(HashMap<String, List<String>> actionTypes, ActionInformation information) {
        actionTypes.forEach((actionType, actions) -> {
            Action actionEnum;
            try {
                actionEnum = Action.valueOf(actionType.toUpperCase());
            } catch (IllegalArgumentException exception) {
                Bukkit.getLogger().warning("The action " + actionType + " is not a valid action! Example: PARTICLE (HMCWraps action configuration)");
                return;
            }
            actions.forEach(action -> {
                information.setArguments(action);
                push(actionEnum, information);
            });
        });
    }

    /**
     * Push all unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushUnwrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("unwrap")) {
            pushFromConfig(wrap.getActions().get("unwrap"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushWrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("wrap")) {
            pushFromConfig(wrap.getActions().get("wrap"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all physical wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushPhysicalUnwrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("unwrap-physical")) {
            pushFromConfig(wrap.getActions().get("unwrap-physical"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all physical unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushPhysicalWrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("wrap-physical")) {
            pushFromConfig(wrap.getActions().get("wrap-physical"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all virtual wrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushVirtualUnwrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("unwrap-virtual")) {
            pushFromConfig(wrap.getActions().get("unwrap-virtual"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all virtual unwrap actions to all subscribers.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushVirtualWrap(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("wrap-virtual")) {
            pushFromConfig(wrap.getActions().get("wrap-virtual"), new WrapActionInformation(wrap, player, ""));
        }
    }

    /**
     * Push all preview actions to all subscribers from the config.
     *
     * @param wrap   The wrap
     * @param player the player
     */
    public void pushPreview(Wrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("preview")) {
            pushFromConfig(wrap.getActions().get("preview"), new WrapActionInformation(wrap, player, ""));
        }
    }

}
