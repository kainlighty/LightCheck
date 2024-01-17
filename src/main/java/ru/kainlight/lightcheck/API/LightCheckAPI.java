package ru.kainlight.lightcheck.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.Main;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LightCheckAPI {

    private LightCheckAPI() {}

    private static final LightCheckAPI lightCheckAPI = new LightCheckAPI();
    public static LightCheckAPI get() {
        return lightCheckAPI;
    }

    /**
     * 1 Player - Inspector
     * <p>
     * 2 Player - Player
     */
    @Getter
    private final Set<CheckedPlayer> checkedPlayers = new HashSet<>();

    public void check(Player player, Player inspector) {
        if (player == null || inspector == null) return;

        var event = new LightCheckAPI.PlayerCheckEvent(player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        CheckedPlayer checkedPlayer = new CheckedPlayer(player, inspector, player.getLocation());
        LightCheckAPI.get().getCheckedPlayers().add(checkedPlayer);

        player.setInvulnerable(true);
        Main.getInstance().getRunnables().start(checkedPlayer);
        checkedPlayer.teleportToInspector();
    }

    public boolean isChecking(CheckedPlayer checkedPlayer) {
        return this.getCheckedPlayers().contains(checkedPlayer);
    }

    public boolean isChecking(Player player) {
        return this.getCheckedPlayer(player).isPresent();
    }
    public boolean isCheckingByInspector(Player inspector) {
        return this.getCheckedPlayerByInspector(inspector).isPresent();
    }

    public Optional<CheckedPlayer> getCheckedPlayer(Player player) {
        return this.getCheckedPlayers().stream().filter(f -> f.getPlayer().equals(player)).findAny();
    }

    public Optional<CheckedPlayer> getCheckedPlayerByInspector(Player inspector) {
        return this.getCheckedPlayers().stream().filter(f -> f.getInspector().equals(inspector)).findAny();
    }

    public void stopAll() {
        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            this.getCheckedPlayer(online).ifPresent(CheckedPlayer::disprove);
        });
    }




    // * -- EVENTS -- * \\
    public static class PlayerCheckEvent extends PlayerEvent implements Cancellable {

        @Getter
        private static final HandlerList handlerList = new HandlerList();

        private final Player player;

        @Getter @Setter
        private boolean isCancelled = false;

        public PlayerCheckEvent(@NotNull Player player) {
            super(player);
            this.player = player;
        }

        public Optional<CheckedPlayer> getCheckedPlayer() {
            return LightCheckAPI.get().getCheckedPlayer(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlerList;
        }
    }

    public static class PlayerApproveCheckEvent extends PlayerEvent implements Cancellable {

        @Getter
        private static final HandlerList handlerList = new HandlerList();

        private final Player player;

        @Getter @Setter
        private boolean isCancelled = false;

        public PlayerApproveCheckEvent(@NotNull Player player) {
            super(player);
            this.player = player;
        }

        public Optional<CheckedPlayer> getCheckedPlayer() {
            return LightCheckAPI.get().getCheckedPlayer(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlerList;
        }
    }

    public static class PlayerDisproveCheckEvent extends PlayerEvent implements Cancellable {

        @Getter
        private static final HandlerList handlerList = new HandlerList();

        private final Player player;

        @Getter @Setter
        private boolean isCancelled = false;

        public PlayerDisproveCheckEvent(@NotNull Player player) {
            super(player);
            this.player = player;
        }

        public Optional<CheckedPlayer> getCheckedPlayer() {
            return LightCheckAPI.get().getCheckedPlayer(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlerList;
        }
    }

}
