package ru.kainlight.lightcheck.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.COMMON.Others;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

public final class CheckedPlayer {

    @Getter
    private final Player player;
    @Getter private final Player inspector;
    @Getter @Setter private Long timer;
    private boolean hasTimer;
    @Getter private final Location previousLocation;

    CheckedPlayer(Player player, Player inspector, Location previousLocation) {
        this.player = player;
        this.inspector = inspector;
        this.previousLocation = previousLocation;
        this.timer = Main.getInstance().getConfig().getLong("settings.timer");
    }

    public void approve() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new LightCheckAPI.PlayerApproveCheckEvent(player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        LightPlayer.of(player).clearTitle();

        Main.getInstance().getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void disprove() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new LightCheckAPI.PlayerDisproveCheckEvent(player);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        LightPlayer.of(player).clearTitle();
        teleportBack();
        Main.getInstance().getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void teleportToInspector() {
        boolean teleportToStaff = Main.getInstance().getConfig().getBoolean("abilities.teleport-to-staff");
        if (!teleportToStaff) return;
        if (player == null || getInspector() == null) return;

        Location groundLocation = Others.get().getGroundLocation(getInspector().getLocation());
        player.teleport(groundLocation);
    }

    public void teleportBack() {
        boolean teleportToStaff = Main.getInstance().getConfig().getBoolean("abilities.teleport-to-staff");
        boolean teleportBack = Main.getInstance().getConfig().getBoolean("abilities.teleport-back");
        if (!teleportToStaff && !teleportBack) return;
        if(player == null) return;

        player.teleport(getPreviousLocation());
    }

    public void startTimer() {
        this.hasTimer = true;
        Main.getInstance().getRunnables().startTimerScheduler(this);
    }

    public boolean hasTimer() {
        return hasTimer;
    }

    public void stopTimer() {
        if (hasTimer()) {
            hasTimer = false;
            Main.getInstance().getRunnables().stopTimer(this);
        }
    }
}
