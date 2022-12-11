package de.skyslycer.hmcwraps.actions.register;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleSubtitle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleText;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetTitleTimes;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.actions.Action;
import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.actions.information.WrapActionInformation;
import de.skyslycer.hmcwraps.util.StringUtil;
import java.util.Arrays;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DefaultActionRegister {

    private final HMCWraps plugin;

    public DefaultActionRegister(HMCWraps plugin) {
        this.plugin = plugin;
    }

    public void register() {
        registerParticle();
        registerParticleMulti();
        registerSound();
        registerTitle();
        registerSubtitle();
        registerActionBar();
        registerMessage();
        registerCommand();
        registerConsoleCommand();
    }

    public void registerParticle() {
        plugin.getActionHandler().subscribe(Action.PARTICLE, (information) -> {
            // TODO: Add particles
            /*
            var split = information.getArguments().split(" ");
            if (checkSplit(split, 1, "particle", "HEART")) {
                return;
            }
             */
        });
    }

    public void registerParticleMulti() {
        plugin.getActionHandler().subscribe(Action.PARTICLE_MULTI, (information) -> {
            // TODO: add particles
        });
    }

    public void registerSound() {
        plugin.getActionHandler().subscribe(Action.SOUND, (information) -> {
            var player = information.getPlayer();
            var split = information.getArguments().split(" ");
            if (checkSplit(split, 1, "sound", "ENTITY_VILLAGER_HURT")) return;
            var sound = Sound.ENTITY_VILLAGER_HURT;
            try {
                sound = Sound.valueOf(split[0]);
            } catch (IllegalArgumentException ignored) {
                Bukkit.getLogger().warning("The sound " + split[0] + " is not a valid sound! Example: ENTITY_VILLAGER_HURT (HMCWraps action configuration)");
            }
            if (split.length == 3) {
                try {
                    player.playSound(player.getLocation(), sound, Float.parseFloat(split[1]), Float.parseFloat(split[2]));
                    return;
                } catch (NumberFormatException exception) {
                    Bukkit.getLogger().warning("The sound " + split[1] + " or " + split[2] + " is not a valid float number! Example: 1.0 (HMCWraps action configuration)");
                }
            }
            player.playSound(player.getLocation(), sound, 1, 1);
        });
    }

    public void registerTitle() {
        plugin.getActionHandler().subscribe(Action.TITLE, (information -> {
            var player = information.getPlayer();
            var split = information.getArguments().split(" ");
            if (checkSplit(split, 4, "title", "0.5 4.0 1.0 message")) return;
            var message = String.join(" ", Arrays.copyOfRange(split, 3, split.length));
            setTitleTimes(player, split);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSetTitleText(StringUtil.parseComponent(player, parseMessage(information, message))));
        }));
    }

    public void registerSubtitle() {
        plugin.getActionHandler().subscribe(Action.SUBTITLE, (information -> {
            var player = information.getPlayer();
            var split = information.getArguments().split(" ");
            if (checkSplit(split, 4, "title", "0.5 4.0 1.0")) return;
            var message = String.join(" ", Arrays.copyOfRange(split, 3, split.length));
            setTitleTimes(player, split);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSetTitleSubtitle(StringUtil.parseComponent(player, parseMessage(information, message))));
        }));
    }

    private void setTitleTimes(Player player, String[] split) {
        var fadeIn = 5;
        var hold = 40;
        var fadeOut = 10;
        try {
            fadeIn = Math.round(Float.parseFloat(split[0]) * 20);
            hold = Math.round(Float.parseFloat(split[1]) * 20);
            fadeOut = Math.round(Float.parseFloat(split[2]) * 20);
        } catch (NumberFormatException exception) {
            Bukkit.getLogger().warning("The title duration " + split[0] + " or " + split[1] + " or " + split[2] + " is not a valid float number! Example: 1.0 (HMCWraps action configuration)");
        }
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerSetTitleTimes(fadeIn, hold, fadeOut));
    }

    private void registerActionBar() {
        plugin.getActionHandler().subscribe(Action.SUBTITLE, (information -> {
            if (checkSplit(information.getArguments().split(" "), 1, "actionbar", "message")) return;
            information.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, StringUtil.parse(information.getPlayer(), parseMessage(information)));
        }));
    }

    public void registerMessage() {
        plugin.getActionHandler().subscribe(Action.MESSAGE, (information) -> {
            if (checkSplit(information.getArguments().split(" "), 1, "message", "message")) return;
            StringUtil.send(information.getPlayer(), parseMessage(information));
        });
    }

    public void registerCommand() {
        plugin.getActionHandler().subscribe(Action.COMMAND, (information) -> {
            if (checkSplit(information.getArguments().split(" "), 1, "command", "say HMCWraps")) return;
            var player = information.getPlayer();
            Bukkit.dispatchCommand(player, parseCommand(information));
        });
    }

    public void registerConsoleCommand() {
        plugin.getActionHandler().subscribe(Action.CONSOLE_COMMAND, (information) -> {
            if (checkSplit(information.getArguments().split(" "), 1, "console command", "kill <player>")) return;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseCommand(information));
        });
    }

    private boolean checkSplit(String[] split, int length, String action, String example) {
        if (split.length < length) {
            Bukkit.getLogger().warning("The " + action + " action needs at least " + length + " arguments! Example: " + example + " (HMCWraps action configuration)");
            return true;
        }
        return false;
    }

    private String parseCommand(ActionInformation information) {
        var string = parseMessage(information);
        if (string.startsWith("/")) {
            string = string.substring(1);
        }
        return string;
    }

    private String parseMessage(ActionInformation information, String message) {
        var player = information.getPlayer();
        var string = StringUtil.replacePlaceholders(player, message.replace("<player>", player.getName()));
        if (information instanceof WrapActionInformation wrapInformation) {
            string = string.replace("<wrap_id>", wrapInformation.getWrap().getUuid()).replace("<wrap>", wrapInformation.getWrap().getName());
        }
        return string;
    }

    private String parseMessage(ActionInformation information) {
        return parseMessage(information, information.getArguments());
    }

}
