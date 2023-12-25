package ru.kainlight.lightcheck.UTILS;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightcheck.Main;

import java.time.Duration;
import java.util.Map;

@Getter
public final class Bossbar {

    private final Main plugin;

    private final Player player;
    private final BossBar bossBar;
    private final long timer;

    private final boolean enabled = Main.getInstance().getConfig().getBoolean("settings.bossbar");

    public Bossbar(Main plugin, Player player, long timer) {
        this.plugin = plugin;
        this.player = player;
        this.timer = timer;
        this.bossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
        bossBar.setProgress(1.0);
    }

    public boolean show() {
        if (!enabled) return false;
        Long playerTimer = LightCheckAPI.get().getTimer().get(player);

        if(player == null) return false;

        if (playerTimer == null || playerTimer <= 0) {
            this.hide();
            return false;
        }

        if (!LightCheckAPI.get().isChecking(player)) {
            this.hide();
            return false;
        }

        String bossbarText = plugin.getMessageConfig().getConfig().getString("screen.bossbar");
        if (bossbarText == null || bossbarText.isEmpty()) return false;
        String newName = bossbarText.replace("<seconds>", playerTimer.toString());
        bossBar.setTitle(Parser.get().hexString(newName));
        bossBar.setProgress((double) playerTimer / timer);

        bossBar.addPlayer(player);
        return true;
    }

    public void hide() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        String bossbarText = plugin.getMessageConfig().getConfig().getString("screen.disprove-title");
        if (bossbarText == null || bossbarText.isEmpty()) {
            this.bossBar.removePlayer(player);
            return;
        }

        bossBar.setTitle(Parser.get().hexString(bossbarText));
        bossBar.setProgress(0.0);

        scheduler.runTaskLater(plugin, () -> {
            this.bossBar.removePlayer(player);

            Map<Player, Integer> sch = plugin.getRunnables().bossbarScheduler;
            Integer remove = sch.get(player);
            if(remove == null) return;
            scheduler.cancelTask(remove);
            sch.remove(player);
        }, 20L * 2);

    }

}
