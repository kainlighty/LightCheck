package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import ru.kainlight.lightcheck.Main;

@SuppressWarnings("deprecation")
public final class Initiators {

    private static final String name = Main.getInstance().getDescription().getName();
    private static final String version = Main.getInstance().getDescription().getVersion();

    public static void startPluginMessage() {
        Parser.get().logger("");
        Parser.get().logger("&c » &f" + name + " enabled");
        Parser.get().logger("&c » &fVersion: " + version);
        new GitHubUpdater(Main.getInstance()).start();
        //Parser.get().logger("&c » &fAuthor: kainlight");
        Parser.get().logger("");
    }

    public static void stopPluginMessage() {
        Parser.get().logger("&c » &f" + name + " disabled");
    }
}
