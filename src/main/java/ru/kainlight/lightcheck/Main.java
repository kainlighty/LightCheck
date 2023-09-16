package ru.kainlight.lightcheck;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightcheck.COMMANDS.Check;
import ru.kainlight.lightcheck.COMMON.lightlibrary.CONFIGS.CustomConfig;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Initiators;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Messenger;
import ru.kainlight.lightcheck.EVENTS.CheckedListener;
import ru.kainlight.lightcheck.UTILS.Runnables;

@Getter
@Internal
@SuppressWarnings("all")
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public CustomConfig messageConfig;
    private Messenger messenger;
    private Runnables runnables;

    @Override
    public void onLoad() {
        saveDefaultConfig();
        CustomConfig.saveLanguages(this,"language");
    }

    @Override
    public void onEnable() {
        instance = this;

        messenger = new Messenger();
        runnables = new Runnables(this);

        getCommand("lightcheck").setExecutor(new Check(this));
        getServer().getPluginManager().registerEvents(new CheckedListener(this), this);

        Initiators.startPluginMessage(getDescription());
    }

    @Override
    public void onDisable() {
        Initiators.stopPluginMessage(getDescription().getName());
    }

}
