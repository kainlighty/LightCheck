package ru.kainlight.lightcheck.UTILS;

import org.bukkit.entity.Player;
import ru.kainlight.lightcheck.API.CheckedPlayer;
import ru.kainlight.lightcheck.API.LightCheckAPI;
import ru.kainlight.lightcheck.COMMON.lightlibrary.LightPlayer;
import ru.kainlight.lightcheck.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Runnables {

    private final Main plugin;

    public Map<Player, Integer> messageChatTimer = new HashMap<>();
    public Map<Player, Integer> messageScreenTimer = new HashMap<>();
    public Map<Player, Integer> clockTimerScheduler = new HashMap<>();

    public Runnables(Main plugin) {
        this.plugin = plugin;
    }

    public void start(Player player) {
        if(player == null) return;

        long timer = plugin.getConfig().getLong("settings.timer");

        startTimerScheduler(player, timer);
        startChatMessageScheduler(player);
        startScreenMessageScheduler(player, timer);
    }

    public void stopMessages(Player player) {
        if (player == null) return;

        plugin.getServer().getScheduler().cancelTask(messageChatTimer.get(player));
        plugin.getServer().getScheduler().cancelTask(messageScreenTimer.get(player));
        messageChatTimer.remove(player);
        messageScreenTimer.remove(player);
    }

    public void stopTimer(Player player) {
        if (player == null) return;

        Integer taskID = clockTimerScheduler.remove(player);
        plugin.getServer().getScheduler().cancelTask(taskID);
        LightCheckAPI.get().getTimer().remove(player);
    }

    public void stopAll(Player player) {
        stopMessages(player);

        if(clockTimerScheduler.get(player) != null) {
            stopTimer(player);
        }
    }

    public void startTimerScheduler(Player player, long timer) {
        CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);

        checkedPlayer.setTimer(timer);

        if(checkedPlayer.getTimer() != null) {
            clockTimerScheduler.put(player, plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                if (player == null) return;

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
                        if(!getApproveCommands.isEmpty()) {
                            getApproveCommands.forEach(commands -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.replace("<player>", checkedPlayer.getPlayer().getName())));
                        }
                    });

                    return;
                }

            }, 0, 20L).getTaskId());
        }
    }

    public void startChatMessageScheduler(Player player) {
        long schedulerTimer = plugin.getConfig().getLong("settings.message-timer") * 20L;
        messageChatTimer.put(player, plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if(player == null) return;

            this.chatMessage(player);
        }, 0L, schedulerTimer).getTaskId());
    }

    public void startScreenMessageScheduler(Player player, long timer) {
        Bossbar bossBar = new Bossbar(plugin, timer);

        messageScreenTimer.put(player, plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if(player == null) return;

            this.screenMessages(player);
            bossBar.sendBossbar(player);
        }, 0L, 20L).getTaskId());
    }


    private void chatMessage(Player player) {
        String hoverMessage = plugin.getMessageConfig().getConfig().getString("chat.hover");

        CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);
        Player inspector = checkedPlayer.getInspector();

        if(inspector == null || !inspector.isOnline()) {
            checkedPlayer.disprove();
            return;
        }

        if (checkedPlayer.hasTimer()) {
            Long timer = checkedPlayer.getTimer();
            Long secToMin = TimeUnit.SECONDS.toMinutes(timer);
            List<String> with_timer = plugin.getMessageConfig().getConfig().getStringList("chat.with-timer");

            with_timer.forEach(message -> {
                message = message
                        .replace("<inspector>", inspector.getName())
                        .replace("<minutes>", secToMin.toString());

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

    private void screenMessages(Player player) {
        CheckedPlayer checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player);
        Player inspector = checkedPlayer.getInspector();

        if(inspector == null || !inspector.isOnline()) {
            checkedPlayer.disprove();
            return;
        }

        boolean titleEnabled = plugin.getConfig().getBoolean("settings.title");
        if (titleEnabled) {
            String titleMessage = plugin.getMessageConfig().getConfig().getString("screen.check-title");
            String subTitleMessage = plugin.getMessageConfig().getConfig().getString("screen.check-subtitle");
            LightPlayer.of(player).sendTitle(titleMessage, subTitleMessage, 1, 15, 2);
        }

        boolean actionbarEnabled = plugin.getConfig().getBoolean("settings.actionbar");
        Long timer = checkedPlayer.getTimer();
        if (actionbarEnabled && timer != null) {
            String message = plugin.getMessageConfig().getConfig().getString("screen.actionbar")
                    .replace("<seconds>", timer.toString());
            LightPlayer.of(player).sendActionbar( message);
        }
    }

}
