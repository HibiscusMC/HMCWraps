package de.skyslycer.hmcwraps.actions.information;

import org.bukkit.entity.Player;

public interface IBasicActionInformation extends ActionInformation {

    @Override
    Player getPlayer();

    @Override
    String getArguments();

    @Override
    void setPlayer(Player player);

    @Override
    void setArguments(String arguments);

}
