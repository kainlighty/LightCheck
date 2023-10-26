package ru.kainlight.lightcheck.API;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.COMMON.Others;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

import java.util.List;

public final class CheckedPlayer {

    private final Main plugin = Main.getInstance();

    @Getter
    private final Player player;

    CheckedPlayer(Player player) {
        this.player = player;
    }

    public void call(Player inspector) {
        if (player == null || inspector == null) return;

        var event = new LightCheckAPI.PlayerCheckEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
		
        player.setInvulnerable(true);

        LightCheckAPI.get().getCheckedPlayers().put(inspector, player);
        plugin.getRunnables().start(player);

        LightCheckAPI.get().getPreviousLocation().put(player, player.getLocation());
        teleportToInspector();
    }

    public void approve() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new LightCheckAPI.PlayerApproveCheckEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        LightPlayer.of(player).clearTitle();
        plugin.getRunnables().stopAll(player);
        LightCheckAPI.get().getCheckedPlayers().inverse().remove(player);
        player.setInvulnerable(false);
    }

    public void disprove() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new LightCheckAPI.PlayerDisproveCheckEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        plugin.getRunnables().stopAll(player);
        LightCheckAPI.get().getCheckedPlayers().inverse().remove(player);
        teleportBack();

        player.setInvulnerable(false);
    }

    public Location getPreviousLocation() {
        return LightCheckAPI.get().getPreviousLocation().get(player);
    }

    public void teleportToInspector() {
        boolean teleportToStaff = plugin.getConfig().getBoolean("abilities.teleport-to-staff");
        if (!teleportToStaff) return;
        if (player == null || getInspector() == null) return;

        Location groundLocation = Others.get().getGroundLocation(getInspector().getLocation());
        player.teleport(groundLocation);
    }

    public void teleportBack() {
        boolean teleportToStaff = plugin.getConfig().getBoolean("abilities.teleport-to-staff");
        boolean teleportBack = plugin.getConfig().getBoolean("abilities.teleport-back");
        if (!teleportToStaff && !teleportBack) return;
        if(player == null) return;

        player.teleport(getPreviousLocation());
    }

    public Long getTimer() {
        return LightCheckAPI.get().getTimer().get(player);
    }

    public void setTimer(long count) {
        LightCheckAPI.get().getTimer().put(player, count);
    }

    public void addTime(long count) {
        if(hasTimer()) {
            LightCheckAPI.get().getTimer().put(player, getTimer() + count);
        }
    }

    public boolean hasTimer() {
        return getTimer() != null && getTimer() > 0;
    }

    public void stopTimer() {
        if (hasTimer()) plugin.getRunnables().stopTimer(player);
    }

    public Player getInspector() {
        return LightCheckAPI.get().getCheckedPlayers().inverse().get(player);
    }
}
