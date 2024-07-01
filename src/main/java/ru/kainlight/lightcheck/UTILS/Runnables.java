package ru.kainlight.lightcheck.UTILS;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.common.lightlibrary.CONFIGS.BukkitConfig;
import ru.kainlight.lightcheck.common.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Runnables {

    private final Main plugin;

    public Map<OfflinePlayer, String> offlineChecks = new HashMap<>();

    public Map<CheckedPlayer, Integer> messageChatTimer = new HashMap<>();
    public Map<CheckedPlayer, Integer> messageScreenTimer = new HashMap<>();
    public Map<CheckedPlayer, Integer> clockTimerScheduler = new HashMap<>();
    public Map<CheckedPlayer, Integer> bossbarScheduler = new HashMap<>();

    public Runnables(Main plugin) {
        this.plugin = plugin;
    }

    public void start(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        checkedPlayer.startTimer();
        startChatMessageScheduler(checkedPlayer);
        startScreenMessageScheduler(checkedPlayer);
        startBossBarScheduler(checkedPlayer);
    }

    public void stopMessages(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        plugin.getServer().getScheduler().cancelTask(messageChatTimer.get(checkedPlayer));
        plugin.getServer().getScheduler().cancelTask(messageScreenTimer.get(checkedPlayer));
        messageChatTimer.remove(checkedPlayer);
        messageScreenTimer.remove(checkedPlayer);
    }

    public void stopTimer(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        Integer taskID = clockTimerScheduler.remove(checkedPlayer);
        plugin.getServer().getScheduler().cancelTask(taskID);
    }

    public void stopAll(CheckedPlayer checkedPlayer) {
        stopMessages(checkedPlayer);

        if (clockTimerScheduler.get(checkedPlayer) != null) {
            checkedPlayer.stopTimer();
        }
    }

    // TODO: Add an alert for the inspector about the imminent end of the timer or common bossbar
    public void startTimerScheduler(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        if (checkedPlayer.getTimer() != null) {
            clockTimerScheduler.put(checkedPlayer, plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if (checkedPlayer.getPlayer() == null) return;

                if (checkedPlayer.getInspector() == null) {
                    checkedPlayer.disprove();
                    return;
                }

                Long timerValue = checkedPlayer.getTimer();
                if (timerValue >= 1) {
                    checkedPlayer.setTimer(timerValue - 1);
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        checkedPlayer.approve();
                        List<String> getApproveCommands = plugin.getConfig().getStringList("commands.approve");
                        if (!getApproveCommands.isEmpty()) {
                            getApproveCommands.forEach(commands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.replace("<player>", checkedPlayer.getPlayer().getName())));
                        }
                    });

                    return;
                }

            }, 0, 20L).getTaskId());
        }
    }

    public void startChatMessageScheduler(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        long schedulerTimer = plugin.getConfig().getLong("settings.message-timer") * 20L;
        messageChatTimer.put(checkedPlayer, plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (checkedPlayer.getPlayer() == null) return;

            this.chatMessage(checkedPlayer);
        }, 0L, schedulerTimer).getTaskId());
    }

    public void startScreenMessageScheduler(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        messageScreenTimer.put(checkedPlayer, plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (checkedPlayer.getPlayer() == null) return;

            this.screenMessages(checkedPlayer);
        }, 0L, 20L).getTaskId());
    }

    public void startBossBarScheduler(CheckedPlayer checkedPlayer) {
        if (checkedPlayer == null) return;

        Bossbar bossBar = new Bossbar(plugin, checkedPlayer);
        bossbarScheduler.put(checkedPlayer, plugin.getServer().getScheduler().runTaskTimer(plugin, bossBar::show, 0L, 20L).getTaskId());
    }

    private void chatMessage(CheckedPlayer checkedPlayer) {
        Player player = checkedPlayer.getPlayer();
        Player inspector = checkedPlayer.getInspector();

        if (inspector == null || !inspector.isOnline()) {
            checkedPlayer.disprove();
            return;
        }

        String hoverMessage = plugin.getMessageConfig().getConfig().getString("chat.hover");
        if (checkedPlayer.hasTimer()) {
            Long timer = checkedPlayer.getTimer();
            Long secToMin = TimeUnit.SECONDS.toMinutes(timer);
            List<String> with_timer = plugin.getMessageConfig().getConfig().getStringList("chat.with-timer");

            with_timer.forEach(message -> {
                message = message
                        .replace("<inspector>", inspector.getName())
                        .replace("<minutes>", secToMin.toString())
                        .replace("<seconds>", timer.toString());

                LightPlayer.of(player).sendClickableHoverMessage(message, hoverMessage, "/check confirm");
            });
        } else {
            List<String> without_timer = plugin.getMessageConfig().getConfig().getStringList("chat.without-timer");
            without_timer.forEach(message -> {
                message = message.replace("<inspector>", inspector.getName());

                LightPlayer.of(player).sendClickableHoverMessage(message, hoverMessage, "/check confirm");
            });
        }
    }

    private void screenMessages(CheckedPlayer checkedPlayer) {
        Player player = checkedPlayer.getPlayer();
        Player inspector = checkedPlayer.getInspector();

        if (inspector == null || !inspector.isOnline()) {
            checkedPlayer.disprove();
            return;
        }

        Long timer = checkedPlayer.getTimer();
        if (timer != null) {
            boolean titleEnabled = plugin.getConfig().getBoolean("settings.title");
            if (titleEnabled) {
                String titleMessage = plugin.getMessageConfig().getConfig().getString("screen.check-title");
                String subTitleMessage = plugin.getMessageConfig().getConfig().getString("screen.check-subtitle");
                LightPlayer.of(player).sendTitle(titleMessage, subTitleMessage, 1, 15, 2);
            }

            boolean actionbarEnabled = plugin.getConfig().getBoolean("settings.actionbar");
            if (actionbarEnabled) {
                String message = plugin.getMessageConfig().getConfig().getString("screen.actionbar").replace("<seconds>", timer.toString());
                LightPlayer.of(player).sendActionbar(message);
            }
        }

    }

    public void addLog(String username, String text) {
        boolean enabled = plugin.getConfig().getBoolean("settings.logging", false);
        if(!enabled) return;

        text = text.replaceAll("&.", "").replaceAll("#.", "");

        BukkitConfig log = new BukkitConfig(plugin, "logs", username + ".yml", false);
        List<String> list = log.getConfig().getStringList("log");
        list.add(text);

        log.getConfig().set("log", list);
        log.saveConfig();
    }

    public void sendPunishmentCommand(Player player, String alias) {
        final ConsoleCommandSender console = plugin.getServer().getConsoleSender();
        List<String> commands = plugin.getConfig().getStringList("commands." + alias);

        if(!commands.isEmpty()) {
            commands.forEach(command ->
                    plugin.getServer().dispatchCommand(console, command.replace("<player>", player.getName()))
            );
        }
    }


}
