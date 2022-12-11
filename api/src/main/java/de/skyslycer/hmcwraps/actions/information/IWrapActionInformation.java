package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.IWrap;
import org.bukkit.entity.Player;

public interface IWrapActionInformation extends ActionInformation {

    IWrap getWrap();

    @Override
    Player getPlayer();

    @Override
    String getArguments();

    void setWrap(IWrap wrap);

    @Override
    void setPlayer(Player player);

    @Override
    void setArguments(String arguments);

}
