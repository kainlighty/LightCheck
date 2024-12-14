package ru.kainlight.lightcheck.API.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.API.LightCheckAPI;

import java.util.Optional;

public final class PlayerApproveCheckEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private boolean isCancelled = false;

    public PlayerApproveCheckEvent(@NotNull Player player) {
        super(player);
        this.player = player;
    }

    public Optional<CheckedPlayer> getCheckedPlayer() {
        return LightCheckAPI.get().getCheckedPlayer(player);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.isCancelled = value;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
