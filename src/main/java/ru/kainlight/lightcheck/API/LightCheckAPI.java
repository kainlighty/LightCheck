package ru.kainlight.lightcheck.API;

import com.google.common.collect.HashBiMap;
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

import java.util.HashMap;
import java.util.Map;
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
    private final HashBiMap<Player, Player> checkedPlayers = HashBiMap.create();
    @Getter
    private final ConcurrentHashMap<Player, Long> timer = new ConcurrentHashMap<>();
    @Getter
    private final Map<Player, Location> previousLocation = new HashMap<>();

    public boolean isChecking(Player player) {
        if(player == null) return false;
        return getCheckedPlayers().containsValue(player);
    }

    public CheckedPlayer getCheckedPlayer(Player player) {
        if(!isChecking(player) && player == null) return null;

        return new CheckedPlayer(player);
    }

    public CheckedPlayer getCheckedPlayerByInspector(Player inspector) {
        Player player = getCheckedPlayers().get(inspector);
        if(!isChecking(player) && player == null && inspector == null) return null;

        return new CheckedPlayer(player);
    }

    public void stopAll() {
        Bukkit.getServer().getOnlinePlayers().forEach(online -> {
            if(isChecking(online)) {
                Main.getInstance().getRunnables().stopMessages(online);

                getCheckedPlayers().remove(online);
                getTimer().remove(online);
                getPreviousLocation().remove(online);
            }
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

        public CheckedPlayer getCheckedPlayer() {
            return new CheckedPlayer(player);
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

        public CheckedPlayer getCheckedPlayer() {
            return new CheckedPlayer(player);
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

        public CheckedPlayer getCheckedPlayer() {
            return new CheckedPlayer(player);
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            return handlerList;
        }
    }

}
