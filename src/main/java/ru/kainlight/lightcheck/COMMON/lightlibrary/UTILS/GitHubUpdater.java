package ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightLib;
import ru.kainlight.lightcheck.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

final class GitHubUpdater {

    private final Main plugin;

    @SuppressWarnings("deprecation")
    GitHubUpdater(Main plugin) {
        this.plugin = plugin;
        this.pluginName = plugin.getDescription().getName();
        this.currentVersion = plugin.getDescription().getVersion();
        this.latestVersion = plugin.getDescription().getVersion();
    }

    private final String pluginName;
    private final String currentVersion;
    private String latestVersion;
    private boolean isPrerelease;
    private String resourceURL;

    private boolean checkForUpdates() {
        if(!plugin.getConfig().getBoolean("update-notification")) return false;

        String githubRepo = "kainlighty/" + pluginName;
        try {
            String url = "https://api.github.com/repos/" + githubRepo + "/releases";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 404) return false;

            InputStreamReader input = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(input);
            StringBuilder responseBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();
            input.close();

            JsonElement jsonElement = new JsonParser().parse(responseBuilder.toString());
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            JsonObject latestRelease = jsonArray.get(0).getAsJsonObject();  // Получаем последний релиз

            latestVersion = latestRelease.get("tag_name").getAsString();
            resourceURL = latestRelease.get("html_url").getAsString();
            isPrerelease = latestRelease.get("prerelease").getAsBoolean(); // Узнаем, является ли последняя версия предварительной версией

            String[] currentVersionParts = currentVersion.split("\\-"),
                    latestVersionParts = latestVersion.split("\\-"),

            // Проверка major.minor.patch версии
            currentVersionNumbers = currentVersionParts[0].split("\\."),
                    latestVersionNumbers = latestVersionParts[0].split("\\.");

            for (int i = 0; i < Math.min(currentVersionNumbers.length, latestVersionNumbers.length); i++) {
                int currentPart = Integer.parseInt(currentVersionNumbers[i]),
                        latestPart = Integer.parseInt(latestVersionNumbers[i]);

                if (currentPart < latestPart) {
                    return true;
                } else if (currentPart > latestPart) {
                    return false;
                }
            }

            // Если версии major.minor.patch равны и последняя версия является пре-релизной версией, а текущая - нет, то последняя версия не считается более новой
            if (latestVersionParts.length > 1 && currentVersionParts.length == 1) {
                return false;
            }

            // Если текущая версия является пре-релизной, а последняя - нет, то последняя версия считается более новой
            if (currentVersionParts.length > 1 && latestVersionParts.length == 1) {
                return true;
            }

            // Если версии major.minor.patch равны и обе являются предварительными версиями, то считать их равными
            return false;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException misc) { // Если версия начинается с букв или содержит буквы, может выкинуть exception. Пусть на всякий будет.
            plugin.getLogger().warning("Error in numbering the new version: " + misc.getMessage());
            return false;
        } catch (Exception e) { // Просто ловлю любые ошибки
            plugin.getLogger().severe("Couldn't check for updates! Stacktrace:");
            e.printStackTrace();
            return false;
        }
    }

    public void start() {
        if (checkForUpdates()) {
            String newVersion = latestVersion;
            if (isPrerelease) newVersion += " #d29922(Pre-release)";

            LightLib.get().logger("&c ! New version found: " + newVersion)
                    .logger("&c ! Recommended for installation: &7" + resourceURL);

        }
    }
}
