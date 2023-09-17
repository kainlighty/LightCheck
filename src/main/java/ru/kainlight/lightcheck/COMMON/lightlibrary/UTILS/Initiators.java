package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import org.bukkit.plugin.Plugin;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightLib;

@SuppressWarnings("deprecation")
public final class Initiators {

    public static void startPluginMessage(Plugin plugin) {
        Messenger.get().logger("");

        Messenger.get()
                .logger("&c » &7" + plugin.getDescription().getName() + " enabled")
                .logger("&c » &7Version: " + plugin.getDescription().getVersion());
        LightLib.startUpdater();

        Messenger.get().logger("");
    }
}
