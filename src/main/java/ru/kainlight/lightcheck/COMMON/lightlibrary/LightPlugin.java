package ru.kainlight.lightcheck.COMMON.lightlibrary;

import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import ru.kainlight.lightcheck.COMMON.lightlibrary.CONFIGS.BukkitConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
public class LightPlugin extends JavaPlugin {

    private final double CONFIG_VERSION = 1.0;
    public static final boolean paper = isPaper();
    public BukkitConfig messageConfig;
	
	public LightPlugin registerListener(@NotNull Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
        return this;
    }

    public LightPlugin registerCommand(@NotNull String command, @NotNull CommandExecutor executor, @Nullable TabCompleter tabCompleter) {
        PluginCommand pluginCommand = this.getCommand(command);
        pluginCommand.setExecutor(executor);
        if(tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        return this;
    }

    public LightPlugin registerCommand(@NotNull String command, @NotNull CommandExecutor executor) {
        PluginCommand pluginCommand = this.getCommand(command);
        pluginCommand.setExecutor(executor);
        return this;
    }


    protected void updateConfig() {
        // Загрузка текущей конфигурации
        FileConfiguration userConfig = getConfig();
        double version = userConfig.getDouble("config-version");
        if(version == CONFIG_VERSION) return;

        // Чтение конфигурации по умолчанию из JAR-файла
        InputStream defaultConfigStream = getResource("config.yml");
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

        getLogger().warning("config.yml updated");
        getConfig().set("config-version", CONFIG_VERSION);
        saveConfig();

        try {
            defaultConfigStream.close();
            inputConfigReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
