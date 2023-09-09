package ru.kainlight.lightcheck.COMMON.lightlibrary;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.kainlight.lightcheck.Main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class LightLib {
    private LightLib() {}

    @SuppressWarnings("all")
    public static void updateConfig(Main plugin) {
        // Загрузка текущей конфигурации
        FileConfiguration userConfig = plugin.getConfig();

        // Чтение конфигурации по умолчанию из JAR-файла
        InputStream defaultConfigStream = plugin.getResource("config.yml");
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

        plugin.saveConfig();
    }

}
