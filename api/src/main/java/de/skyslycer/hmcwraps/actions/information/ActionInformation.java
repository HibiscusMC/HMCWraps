package de.skyslycer.hmcwraps.actions.information;

import org.bukkit.entity.Player;

public interface ActionInformation {

    Player getPlayer();

    String getArguments();

    void setPlayer(Player player);

    void setArguments(String arguments);

}
