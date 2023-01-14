package de.skyslycer.hmcwraps.events;

import de.skyslycer.hmcwraps.serialization.IWrap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemUnwrapEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    boolean isCancelled = false;

    private Player player;
    private ItemStack item;
    private final IWrap wrap;
    private boolean giveBack;

    public ItemUnwrapEvent(ItemStack item, Player player, IWrap wrap, boolean giveBack) {
        this.item = item;
        this.player = player;
        this.wrap = wrap;
        this.giveBack = giveBack;
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

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    @Nullable
    public IWrap getWrap() {
        return wrap;
    }

    public boolean isGiveBack() {
        return giveBack;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setGiveBack(boolean giveBack) {
        this.giveBack = giveBack;
    }

}
