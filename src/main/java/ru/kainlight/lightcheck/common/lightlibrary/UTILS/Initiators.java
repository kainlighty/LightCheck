package ru.kainlight.lightcheck.common.lightlibrary.UTILS;

import org.bukkit.plugin.Plugin;
import ru.kainlight.lightcheck.common.lightlibrary.LightLib;
import ru.kainlight.lightcheck.Main;

@SuppressWarnings("deprecation")
public final class Initiators {

    public static void startPluginMessage(Plugin plugin) {
        LightLib.get()
                .logger("")
                .logger("&c » &7" + plugin.getDescription().getName() + " enabled")
                .logger("&c » &7Version: " + plugin.getDescription().getVersion());
        new GitHubUpdater(Main.getINSTANCE()).start();
        LightLib.get().logger("");
    }
}
