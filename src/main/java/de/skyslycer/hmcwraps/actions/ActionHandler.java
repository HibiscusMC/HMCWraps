package de.skyslycer.hmcwraps.actions;

import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.actions.information.WrapActionInformation;
import de.skyslycer.hmcwraps.serialization.IWrap;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionHandler implements IActionHandler {

    private final HashMap<Action, ActionMethod> actions = new HashMap<>();

    @Override
    public void subscribe(Action action, ActionMethod method) {
        actions.put(action, method);
    }

    @Override
    public void push(Action action, ActionInformation information) {
        if (!actions.containsKey(action)) {
            Bukkit.getLogger().warning("Action " + action.name() + " is not registered! This is a bug! Please report this.");
            return;
        }
        actions.get(action).execute(information);
    }

    @Override
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

    @Override
    public void pushUnwrap(IWrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("unwrap")) {
            pushFromConfig(wrap.getActions().get("unwrap"), new WrapActionInformation(wrap, player, ""));
        }
    }

    @Override
    public void pushWrap(IWrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("wrap")) {
            pushFromConfig(wrap.getActions().get("wrap"), new WrapActionInformation(wrap, player, ""));
        }
    }

    @Override
    public void pushPreview(IWrap wrap, Player player) {
        if (wrap.getActions() != null && wrap.getActions().containsKey("preview")) {
            pushFromConfig(wrap.getActions().get("preview"), new WrapActionInformation(wrap, player, ""));
        }
    }

}
