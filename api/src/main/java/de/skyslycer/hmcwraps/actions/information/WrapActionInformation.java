package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;

public class WrapActionInformation implements ActionInformation {

    private Wrap wrap;
    private Player player;
    private String arguments;

    public WrapActionInformation(Wrap wrap, Player player, String arguments) {
        this.wrap = wrap;
        this.player = player;
        this.arguments = arguments;
    }

    public Wrap getWrap() {
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

    public void setWrap(Wrap wrap) {
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
