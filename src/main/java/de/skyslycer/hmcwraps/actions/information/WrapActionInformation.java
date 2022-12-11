package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.IWrap;
import org.bukkit.entity.Player;

public class WrapActionInformation implements IWrapActionInformation {

    private IWrap wrap;
    private Player player;
    private String arguments;

    public WrapActionInformation(IWrap wrap, Player player, String arguments) {
        this.wrap = wrap;
        this.player = player;
        this.arguments = arguments;
    }

    @Override
    public IWrap getWrap() {
        return wrap;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    @Override
    public void setWrap(IWrap wrap) {
        this.wrap = wrap;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

}
