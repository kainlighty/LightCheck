package ru.kainlight.lightcheck.COMMON.lightlibrary.CONFIGS;

import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class BukkitConfig {
    private final LightPlugin plugin;
    private double CONFIG_VERSION = 1.1;
    private final String fileName;
    private final String subdirectory;
    private File configFile;
    private FileConfiguration fileConfiguration;

    public BukkitConfig(LightPlugin plugin, String fileName) {
        this(plugin, null, fileName);
    }

    public BukkitConfig(LightPlugin plugin, String subdirectory, String fileName) {
        this.plugin = plugin;
        this.subdirectory = subdirectory;
        this.fileName = fileName;
        saveDefaultConfig();
    }


    public void saveDefaultConfig() {
        if (configFile == null) {
            if (subdirectory == null) {
                configFile = new File(plugin.getDataFolder(), fileName);
            } else {
                File subdirectoryFile = new File(plugin.getDataFolder(), subdirectory);
                if (!subdirectoryFile.exists()) {
                    subdirectoryFile.mkdir();
                }
                configFile = new File(subdirectoryFile, fileName);
            }
        }

        if (!configFile.exists()) {
            if (subdirectory != null) {
                plugin.saveResource(subdirectory + File.separator + fileName, false);
            } else {
                plugin.saveResource(fileName, false);
            }
        }
    }

    public void reloadConfig() {
        if (configFile == null) {
            if (subdirectory != null) {
                configFile = new File(plugin.getDataFolder() + File.separator + subdirectory, fileName);
            } else {
                configFile = new File(plugin.getDataFolder(), fileName);
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) return;

        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config to " + configFile);
        }
    }

    public void reloadLanguage(String pathToLang) {
        String lang = plugin.getConfig().getString(pathToLang);
        String fileName = this.fileName.replace(".yml", "");
        if (!lang.equalsIgnoreCase(fileName)) {
            String langFile = lang + ".yml";
            plugin.messageConfig = new BukkitConfig(plugin, "messages", langFile.toLowerCase());
        }
        this.reloadConfig();
    }

    public static void saveLanguages(LightPlugin plugin, String pathToLang) {
        // Получить URL папки messages внутри JAR
        URL url = BukkitConfig.class.getResource("/messages");
        if (url != null) {
            try {
                // Создать подключение к этому URL
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection jarConnection) {
                    // Получить JAR файл
                    JarFile jar = jarConnection.getJarFile();

                    // Получить все элементы JAR файла
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        // Если элемент - файл в папке messages, то сохранить его
                        if (name.startsWith("messages/") && name.endsWith(".yml")) {
                            String fileName = name.substring("messages/".length());
                            plugin.messageConfig = new BukkitConfig(plugin, "messages", fileName);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String langFile = plugin.getConfig().getString(pathToLang) + ".yml";
        plugin.messageConfig = new BukkitConfig(plugin, "messages", langFile.toLowerCase());
        plugin.messageConfig.reloadConfig();
    }

    @SuppressWarnings("all")
    public void updateConfig() {
        // Загрузка текущей конфигурации
        FileConfiguration userConfig = this.getConfig();
        Double version = userConfig.getDouble("config-version");
        if(version == null) return;
        if(version == CONFIG_VERSION) return;

        InputStream defaultConfigStream;
        // Чтение конфигурации по умолчанию из JAR-файла
        if(subdirectory != null) {
            defaultConfigStream = plugin.getResource(subdirectory + "/" + fileName);
        } else {
            defaultConfigStream = plugin.getResource(fileName);
        }
        InputStreamReader inputConfigReader = new InputStreamReader(defaultConfigStream, StandardCharsets.UTF_8);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(inputConfigReader);

        // Добавление отсутствующих значений из конфигурации по умолчанию и удаление удаленных ключей
        defaultConfig.getKeys(true).forEach(key -> {
            // Если у пользователя нет такого ключа или его значение является значением по умолчанию, добавляем его
            if (!userConfig.contains(key) || userConfig.get(key).equals(defaultConfig.get(key))) {
                userConfig.set(key, defaultConfig.get(key));
            }
        });

        userConfig.getKeys(true).forEach(key -> {
            if (!defaultConfig.contains(key)) {
                userConfig.set(key, null);
            }
        });

        plugin.getLogger().warning(fileName + " updated");
        getConfig().set("config-version", CONFIG_VERSION);
        saveConfig();

        try {
            defaultConfigStream.close();
            inputConfigReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConfigVersion(double ver) {
        this.CONFIG_VERSION = ver;
    }

}