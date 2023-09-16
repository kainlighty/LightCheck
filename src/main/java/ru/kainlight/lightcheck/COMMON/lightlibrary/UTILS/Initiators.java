package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import org.bukkit.plugin.PluginDescriptionFile;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightLib;

public final class Initiators {
    public static void startPluginMessage(PluginDescriptionFile d) {
        Messenger.get().logger("");

        Messenger.get()
                .logger("&c » &f" + d.getName() + " enabled")
                .logger("&c » &fVersion: " + d.getVersion());

        LightLib.startUpdater();
        Messenger.get().logger("");
    }

    public static void stopPluginMessage(String name) {
        Messenger.get().logger("&c » &f" + name + " disabled");
    }
}
