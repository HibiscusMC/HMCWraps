package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;

public class WrapActionInformation extends BasicActionInformation implements WrapInformation {

    private Wrap wrap;

    public WrapActionInformation(Wrap wrap, Player player, String arguments) {
        super(player, arguments);
        this.wrap = wrap;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public void setWrap(Wrap wrap) {
        this.wrap = wrap;
    }

}
