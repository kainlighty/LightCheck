package ru.kainlight.lightcheck.UTILS

import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.LightConfig
import ru.kainlight.lightlibrary.multiActionbar
import ru.kainlight.lightlibrary.multiMessage
import ru.kainlight.lightlibrary.multiTitle
import java.io.File
import java.util.concurrent.TimeUnit

class Runnables(val plugin: Main) {

    val offlineChecks: MutableMap<OfflinePlayer, String> = mutableMapOf()

    val messageChatTimer: MutableMap<CheckedPlayer, Int> = mutableMapOf()
    val messageScreenTimer: MutableMap<CheckedPlayer, Int> = mutableMapOf()
    val clockTimerScheduler: MutableMap<CheckedPlayer, Int> = mutableMapOf()
    val bossbarScheduler: MutableMap<CheckedPlayer, Int> = mutableMapOf()

    fun start(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        checkedPlayer.startTimer()
        startChatMessageScheduler(checkedPlayer)
        startScreenMessageScheduler(checkedPlayer)
        startBossBarScheduler(checkedPlayer)
    }

    fun stopMessages(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        plugin.server.scheduler.cancelTask(messageChatTimer.get(checkedPlayer)!!)
        plugin.server.scheduler.cancelTask(messageScreenTimer.get(checkedPlayer)!!)
        messageChatTimer.remove(checkedPlayer)
        messageScreenTimer.remove(checkedPlayer)
    }

    fun stopTimer(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val taskID: Int = clockTimerScheduler.remove(checkedPlayer)!!
        plugin.server.scheduler.cancelTask(taskID)
    }

    fun stopAll(checkedPlayer: CheckedPlayer) {
        stopMessages(checkedPlayer)

        if (clockTimerScheduler.get(checkedPlayer) != null) {
            checkedPlayer.stopTimer()
        }
    }

    @Suppress("WARNINGS")
    // TODO: Add an alert for the inspector about the imminent end of the timer or common bossbar
    fun startTimerScheduler(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        if (checkedPlayer.timer != null) {
            val server = plugin.server
            clockTimerScheduler.put(checkedPlayer, server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
                if (checkedPlayer.player == null) return@Runnable

                if (checkedPlayer.inspector == null) {
                    checkedPlayer.disprove()
                    return@Runnable
                }

                val timerValue: Long = checkedPlayer.timer
                if (timerValue >= 1) {
                    checkedPlayer.timer = timerValue - 1
                } else {
                    server.scheduler.runTask(plugin, Runnable {
                        checkedPlayer.approve()

                        val getApproveCommands: List<String> = plugin.config.getStringList("commands.approve")
                        if (!getApproveCommands.isEmpty()) {
                            getApproveCommands.forEach {
                                server.dispatchCommand(server.consoleSender, it.replace("#player#", checkedPlayer.player.name))
                            }
                        }

                    })
                    return@Runnable
                }
            }, 0L, 20L).taskId)
        }
    }

    fun startChatMessageScheduler(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val schedulerTimer: Long = plugin.config.getLong("settings.message-timer") * 20L
        messageChatTimer.put(checkedPlayer, plugin.server.scheduler.runTaskTimerAsynchronously(plugin, Runnable {
            if (checkedPlayer.player == null) return@Runnable

            this.chatMessage(checkedPlayer)
        }, 0L, schedulerTimer).taskId)
    }

    fun startScreenMessageScheduler(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        messageScreenTimer.put(checkedPlayer, plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            if (checkedPlayer.player == null) return@Runnable

            this.screenMessages(checkedPlayer)
        }, 0L, 20L).taskId)
    }

    fun startBossBarScheduler(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val bossBar: Bossbar = Bossbar(plugin, checkedPlayer)
        bossbarScheduler.put(checkedPlayer, plugin.server.scheduler.runTaskTimer(plugin, bossBar::show, 0L, 20L).taskId)
    }

    private fun chatMessage(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val player: Player = checkedPlayer.player
        val inspector: Player? = checkedPlayer.inspector

        if (inspector == null || !inspector.isOnline) {
            checkedPlayer.disprove()
            return;
        }

        val hoverMessage: String? = plugin.getMessageConfig().getString("chat.hover")
        if (checkedPlayer.hasTimer()) {
            val timer: Long = checkedPlayer.timer
            val secToMin: Long = TimeUnit.SECONDS.toMinutes(timer)
            val with_timer: List<String> = plugin.getMessageConfig().getStringList("chat.with-timer")

            with_timer.forEach {
                var message = it
                message = message
                    .replace("#inspector#", inspector.name)
                    .replace("#minutes#", secToMin.toString())
                    .replace("#seconds#", timer.toString())

                if(hoverMessage != null && !hoverMessage.isBlank()) player.getAudience().multiMessage(message, hoverMessage, ClickEvent.Action.RUN_COMMAND, "/check confirm")
                else player.getAudience().multiMessage(message)
            }
        } else {
            val without_timer: List<String> = plugin.getMessageConfig().getStringList("chat.without-timer")
            without_timer.forEach {
                var message = it
                message = message
                    .replace("#inspector#", inspector.name)

                if(hoverMessage != null && !hoverMessage.isBlank()) player.getAudience().multiMessage(message, hoverMessage, ClickEvent.Action.RUN_COMMAND, "/check confirm")
                else player.getAudience().multiMessage(message)
            }
        }
    }

    private fun screenMessages(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val player: Player = checkedPlayer.player
        val inspector: Player? = checkedPlayer.inspector

        if (inspector == null || !inspector.isOnline) {
            checkedPlayer.disprove()
            return;
        }

        val timer: Long? = checkedPlayer.timer
        if (timer != null) {
            val titleEnabled: Boolean = plugin.config.getBoolean("settings.title")
            if (titleEnabled) {
                val titleMessage: String = plugin.getMessageConfig().getString("screen.check-title")!!
                val subTitleMessage: String = plugin.getMessageConfig().getString("screen.check-subtitle")!!
                player.getAudience().multiTitle(titleMessage, subTitleMessage, 1, 15, 2)
            }

            val actionbarEnabled: Boolean = plugin.config.getBoolean("settings.actionbar")
            if (actionbarEnabled) {
                val message: String = plugin.getMessageConfig().getString("screen.actionbar")?.replace("#seconds#", timer.toString())!!
                player.getAudience().multiActionbar(message)
            }
        }
    }

    // $ DEV-7
    fun addLog(username: String, txt: String) {
        val cleanedText = txt
            .replace("""[&][0-9a-fk-or]""".toRegex(), "")
            .replace("""<[^>]+>""".toRegex(), "")
        val enabled: Boolean = plugin.config.getBoolean("settings.logging", false)
        if (!enabled) return

        // !
        val logsFolder = File(plugin.dataFolder, "logs")
        if (!logsFolder.exists()) logsFolder.mkdirs() // Создаем папку logs, если её нет

        val logFile = File(logsFolder, "$username.yml")
        if (!logFile.exists()) {
            logFile.createNewFile() // Создаем файл, если он не существует
        }
        // !

        val logConfig = LightConfig(plugin, "logs", "$username.yml")
        val list: MutableList<String> = logConfig.getConfig().getStringList("log")
        list.add(cleanedText)

        logConfig.getConfig().set("log", list)
        logConfig.saveConfig()
    }

    fun sendPunishmentCommand(player: Player, alias: String) {
        val console: ConsoleCommandSender = plugin.server.consoleSender;
        val commands: List<String> = plugin.config.getStringList("commands.$alias");

        if(commands.isNotEmpty()) {
            commands.forEach {
                plugin.server.dispatchCommand(console, it.replace("#player#", player.name))
            }
        }
    }

}