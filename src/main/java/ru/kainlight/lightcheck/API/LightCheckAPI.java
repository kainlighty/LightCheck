package ru.kainlight.lightcheck.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.events.PlayerCheckEvent;
import ru.kainlight.lightcheck.Main;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    private final Set<CheckedPlayer> checkedPlayers = new HashSet<>();

    public void call(Player player, Player inspector) {
        if (player == null || inspector == null) return;

        CheckedPlayer checkedPlayer = new CheckedPlayer(player, inspector, player.getLocation());
        LightCheckAPI.get().getCheckedPlayers().add(checkedPlayer);

        var event = new PlayerCheckEvent(player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        player.setInvulnerable(true);
        checkedPlayer.teleportToInspector();
        Main.getInstance().getRunnables().start(checkedPlayer);
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
        Bukkit.getServer().getOnlinePlayers().forEach(online -> this.getCheckedPlayer(online).ifPresent(CheckedPlayer::disprove));
    }

    public Set<CheckedPlayer> getCheckedPlayers() {
        return this.checkedPlayers;
    }
}
