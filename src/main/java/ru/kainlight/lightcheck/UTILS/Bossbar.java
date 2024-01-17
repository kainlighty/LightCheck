package ru.kainlight.lightcheck.UTILS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightcheck.Main;

import java.util.Map;

@Getter
public final class Bossbar {

    private final Main plugin;

    private final CheckedPlayer checkedPlayer;
    private final BossBar bossBar;
    private final long timer;
    private final boolean enabled;

    public Bossbar(Main plugin, CheckedPlayer checkedPlayer) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("settings.bossbar");

        this.checkedPlayer = checkedPlayer;
        this.timer = checkedPlayer.getTimer();
        this.bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);

        bossBar.setProgress(1.0);
    }

    public boolean show() {
        if (!enabled) return false;
        if(checkedPlayer == null) return false;
        Long playerTimer = checkedPlayer.getTimer();

        if (playerTimer == null || playerTimer <= 0) {
            this.hide();
            return false;
        }

        if (!LightCheckAPI.get().isChecking(checkedPlayer)) {
            this.hide();
            return false;
        }

        String bossbarText = plugin.getMessageConfig().getConfig().getString("screen.bossbar");
        if (bossbarText == null || bossbarText.isEmpty()) return false;
        String newName = bossbarText.replace("<seconds>", playerTimer.toString());
        bossBar.setTitle(Parser.get().hexString(newName));
        bossBar.setProgress((double) playerTimer / timer);

        bossBar.addPlayer(checkedPlayer.getPlayer());
        return true;
    }

    public void hide() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        String bossbarText = plugin.getMessageConfig().getConfig().getString("screen.disprove-title");
        if (bossbarText == null || bossbarText.isEmpty()) {
            this.bossBar.removePlayer(checkedPlayer.getPlayer());
            return;
        }

        bossBar.setTitle(Parser.get().hexString(bossbarText));
        bossBar.setProgress(0.0);

        scheduler.runTaskLater(plugin, () -> {
            this.bossBar.removePlayer(checkedPlayer.getPlayer());

            Map<CheckedPlayer, Integer> sch = plugin.getRunnables().bossbarScheduler;
            Integer remove = sch.get(checkedPlayer);
            if(remove == null) return;
            scheduler.cancelTask(remove);
            sch.remove(checkedPlayer);
        }, 20L * 2);

    }

}
