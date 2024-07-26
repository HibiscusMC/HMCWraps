package de.skyslycer.hmcwraps.events;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemWrapEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    boolean isCancelled = false;

    private Wrap wrap;
    private Player player;
    private ItemStack item;
    private boolean physical;

    public ItemWrapEvent(Wrap wrap, ItemStack item, boolean physical, Player player) {
        this.wrap = wrap;
        this.item = item;
        this.physical = physical;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isPhysical() {
        return physical;
    }

    public void setWrap(Wrap wrap) {
        this.wrap = wrap;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setPhysical(boolean physical) {
        this.physical = physical;
    }

}
