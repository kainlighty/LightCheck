package ru.kainlight.lightcheck;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus.Internal;
import ru.kainlight.lightcheck.COMMANDS.Check;
import ru.kainlight.lightcheck.COMMON.lightlibrary.CONFIGS.BukkitConfig;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlugin;
import ru.kainlight.lightcheck.COMMON.lightlibrary.UTILS.Initiators;
import ru.kainlight.lightcheck.EVENTS.CheckedListener;
import ru.kainlight.lightcheck.UTILS.Runnables;

@Getter
@Internal
@SuppressWarnings("all")
public final class Main extends LightPlugin {

    @Getter
    private static Main instance;
    private Runnables runnables;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        BukkitConfig.saveLanguages(this, "language");
        updateConfig();
        messageConfig.updateConfig();
    }

    @Override
    public void onEnable() {
        instance = this;
        runnables = new Runnables(this);

        registerCommand("lightcheck", new Check(this), new Check.Completer(this));
        registerListener(new CheckedListener(this));

        Initiators.startPluginMessage(this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
    }

}
