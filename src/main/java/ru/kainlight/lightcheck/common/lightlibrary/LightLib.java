package ru.kainlight.lightcheck.common.lightlibrary;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.common.lightlibrary.UTILS.Parser;

public final class LightLib {
    private LightLib() {}

    public static LightLib get() {
        return new LightLib();
    }

    public LightLib logger(@NotNull String message) {
        String messageComponent = Parser.get().hexString(message);
        Bukkit.getServer().getConsoleSender().sendMessage(messageComponent);
        return this;
    }

    public boolean isVersion(String number) {
        return Bukkit.getServer().getVersion().contains(number);
    }

    public boolean higher(String version) {
        String serverVersion = Bukkit.getServer().getVersion();
        String[] serverVersionParts = serverVersion.split(" ")[2].split("\\.");
        String[] targetVersionParts = version.split("\\.");

        int serverMajor = Integer.parseInt(serverVersionParts[0]);
        int serverMinor = Integer.parseInt(serverVersionParts[1]);

        int targetMajor = Integer.parseInt(targetVersionParts[0]);
        int targetMinor = Integer.parseInt(targetVersionParts[1]);

        if (serverMajor > targetMajor) {
            return true;
        } else if (serverMajor == targetMajor) {
            return serverMinor >= targetMinor;
        } else {
            return false;
        }
    }

    public boolean lower(String version) {
        String serverVersion = Bukkit.getServer().getVersion();
        String[] serverVersionParts = serverVersion.split(" ")[2].split("\\.");
        String[] targetVersionParts = version.split("\\.");

        int serverMajor = Integer.parseInt(serverVersionParts[0]);
        int serverMinor = Integer.parseInt(serverVersionParts[1]);

        int targetMajor = Integer.parseInt(targetVersionParts[0]);
        int targetMinor = Integer.parseInt(targetVersionParts[1]);

        if (serverMajor < targetMajor) {
            return true;
        } else if (serverMajor == targetMajor) {
            return serverMinor <= targetMinor;
        } else {
            return false;
        }
    }



}
