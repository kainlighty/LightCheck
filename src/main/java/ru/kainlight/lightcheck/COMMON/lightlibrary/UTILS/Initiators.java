package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import org.bukkit.plugin.Plugin;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightLib;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

@SuppressWarnings("deprecation")
public final class Initiators {

    public static void startPluginMessage(Plugin plugin) {
        LightPlayer.registerAudience(plugin);

        LightLib.get().logger("");

        LightLib.get().logger("&c » &7" + plugin.getDescription().getName() + " enabled")
                .logger("&c » &7Version: " + plugin.getDescription().getVersion());
        new GitHubUpdater(Main.getInstance()).start();

        LightLib.get().logger("");
    }
}
