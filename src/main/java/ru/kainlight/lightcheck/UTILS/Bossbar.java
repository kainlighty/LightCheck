package ru.kainlight.lightcheck.UTILS;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Parser;
import ru.kainlight.lightcheck.Main;

public final class Bossbar {

    private final BossBar bossBar;
    private final long timer;

    public Bossbar(long timer) {
        this.timer = timer;
        this.bossBar = BossBar.bossBar(Component.text(""), 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public void sendBossbar(Player player) {
        Long playerTimer = LightCheckAPI.get().getTimer().get(player);

        if(playerTimer == null || playerTimer <= 0) {
            player.hideBossBar(bossBar);
            return;
        }

        String newName = Main.getInstance().getMessageConfig().getConfig().getString("screen.bossbar").replace("<seconds>", playerTimer.toString());
        Component nameComponent = Parser.get().hex(newName);
        bossBar.name(nameComponent);
        bossBar.progress((float) playerTimer / timer);

        player.showBossBar(bossBar);
    }


}
