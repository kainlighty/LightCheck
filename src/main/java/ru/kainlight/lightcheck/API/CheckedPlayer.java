package ru.kainlight.lightcheck.API;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.events.PlayerApproveCheckEvent;
import ru.kainlight.lightcheck.API.events.PlayerDisproveCheckEvent;
import ru.kainlight.lightcheck.common.Others;
import ru.kainlight.lightcheck.common.lightlibrary.LightPlayer;
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
        this.timer = Main.getINSTANCE().getConfig().getLong("settings.timer");
    }

    public void approve() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new PlayerApproveCheckEvent(player);
        Main.getINSTANCE().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        LightPlayer.of(player).clearTitle();
        Main.getINSTANCE().getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void disprove() {
        if(player == null) return;
        if (!LightCheckAPI.get().isChecking(player)) return;

        var event = new PlayerDisproveCheckEvent(player);
        Main.getINSTANCE().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        LightPlayer.of(player).clearTitle();
        teleportBack();
        Main.getINSTANCE().getRunnables().stopAll(this);
        LightCheckAPI.get().getCheckedPlayers().remove(this);
        player.setInvulnerable(false);
    }

    public void teleportToInspector() {
        boolean teleportToStaff = Main.getINSTANCE().getConfig().getBoolean("abilities.teleport-to-staff");
        if (!teleportToStaff) return;
        if (player == null || getInspector() == null) return;

        Location groundLocation = Others.get().getGroundLocation(getInspector().getLocation());
        player.teleport(groundLocation);
    }

    public void teleportBack() {
        boolean teleportToStaff = Main.getINSTANCE().getConfig().getBoolean("abilities.teleport-to-staff");
        boolean teleportBack = Main.getINSTANCE().getConfig().getBoolean("abilities.teleport-back");
        if (!teleportToStaff && !teleportBack) return;
        if(player == null) return;

        player.teleport(getPreviousLocation());
    }

    public boolean startTimer() {
        if(!hasTimer()) {
            this.hasTimer = true;
            Main.getINSTANCE().getRunnables().startTimerScheduler(this);
            return true;
        } else return false;
    }

    public boolean hasTimer() {
        return hasTimer;
    }

    public boolean stopTimer() {
        if (hasTimer()) {
            hasTimer = false;
            Main.getINSTANCE().getRunnables().stopTimer(this);
            return true;
        } else return false;
    }

}
