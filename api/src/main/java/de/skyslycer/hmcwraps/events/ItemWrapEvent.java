package de.skyslycer.hmcwraps.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemWrapEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    boolean isCancelled = false;

    private int modelId;
    private String wrapId;
    private Player player;
    private ItemStack item;
    private boolean physical;
    private boolean giveBack;

    public ItemWrapEvent(Integer modelId, String wrapId, ItemStack item, boolean physical, Player player, boolean giveBack) {
        this.modelId = modelId;
        this.wrapId = wrapId;
        this.item = item;
        this.physical = physical;
        this.player = player;
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

    public int getModelId() {
        return modelId;
    }

    public String getWrapId() {
        return wrapId;
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

    public boolean isGiveBack() {
        return giveBack;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setWrapId(String wrapId) {
        this.wrapId = wrapId;
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

    public void setGiveBack(boolean giveBack) {
        this.giveBack = giveBack;
    }

}
