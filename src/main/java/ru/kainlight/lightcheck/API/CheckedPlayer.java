package ru.kainlight.lightcheck.API;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.events.PlayerApproveCheckEvent;
import ru.kainlight.lightcheck.API.events.PlayerDisproveCheckEvent;
import ru.kainlight.lightcheck.Main;
import ru.kainlight.lightcheck.UTILS.AudienceUtils;
import ru.kainlight.lightcheck.UTILS.GroundLocation;

public final class CheckedPlayer {

    private final Player player;
    private final Player inspector;
    private Long timer;
    private boolean hasTimer;
    private final Location previousLocation;

    CheckedPlayer(Player player, Player inspector, Location previousLocation) {
        this.player = player;
        this.inspector = inspector;
        this.previousLocation = previousLocation;
        this.timer = Main.getInstance().getConfig().getLong("settings.timer");
    }

    public void approve() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        Main plugin = Main.getInstance();

        var event = new PlayerApproveCheckEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        AudienceUtils.INSTANCE.getAudience(plugin, player).clearTitle();
        plugin.getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void disprove() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        Main plugin = Main.getInstance();

        var event = new PlayerDisproveCheckEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        AudienceUtils.INSTANCE.getAudience(plugin, player).clearTitle();
        teleportBack();
        plugin.getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void teleportToInspector() {
        boolean teleportToStaff = Main.getInstance().getConfig().getBoolean("abilities.teleport-to-staff");
        if (!teleportToStaff) return;
        if (player == null || getInspector() == null) return;

        Location location = getInspector().getLocation();
        GroundLocation ground = new GroundLocation(location);
        Location groundLocation = ground.getGroundLocation();

        player.teleport(groundLocation);
    }

    public void teleportBack() {
        boolean teleportToStaff = Main.getInstance().getConfig().getBoolean("abilities.teleport-to-staff");
        boolean teleportBack = Main.getInstance().getConfig().getBoolean("abilities.teleport-back");
        if (!teleportToStaff && !teleportBack) return;
        if(player == null) return;

        player.teleport(getPreviousLocation());
    }

    public boolean startTimer() {
        if(!hasTimer()) {
            this.hasTimer = true;
            Main.getInstance().getRunnables().startTimerScheduler(this);
            return true;
        } else return false;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    public boolean hasTimer() {
        return hasTimer;
    }

    public Long getTimer() {
        return timer;
    }

    public boolean stopTimer() {
        if (hasTimer()) {
            hasTimer = false;
            Main.getInstance().getRunnables().stopTimer(this);
            return true;
        } else return false;
    }

    ///


    public Player getPlayer() {
        return player;
    }

    public Player getInspector() {
        return inspector;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }
}
