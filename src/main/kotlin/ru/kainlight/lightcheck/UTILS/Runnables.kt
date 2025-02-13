package ru.kainlight.lightcheck.UTILS

import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.OfflinePlayer
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.getAudience
import ru.kainlight.lightlibrary.multiActionbar
import ru.kainlight.lightlibrary.multiMessage
import ru.kainlight.lightlibrary.multiTitle
import java.util.concurrent.TimeUnit

internal class Runnables(private val plugin: Main) {

    val offlineChecks: MutableMap<OfflinePlayer, String> = mutableMapOf()

    val messageChatTimer: MutableMap<CheckedPlayer, Int> = mutableMapOf()
    val clockTimerScheduler: MutableMap<CheckedPlayer, Int> = mutableMapOf()
    val messageScreenTimer: MutableMap<Player, Int> = mutableMapOf()
    val bossbarScheduler: MutableMap<Player, Int> = mutableMapOf()

    fun start(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        checkedPlayer.startTimer()
        startChatMessageScheduler(checkedPlayer)
        startScreenMessageScheduler(checkedPlayer)
        startBossBarScheduler(checkedPlayer)
    }

    fun startTimerScheduler(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val task = plugin.runTaskTimerAsynchronously(Runnable {
            if (!checkedPlayer.player.isOnline) return@Runnable

            if (!checkedPlayer.inspector.player.isOnline) {
                checkedPlayer.disprove()
                return@Runnable
            }

            val timerValue: Long = checkedPlayer.timer
            if (timerValue >= 1) {
                checkedPlayer.timer = timerValue - 1
            } else {
                plugin.runTask(Runnable {
                    checkedPlayer.approve()

                    val getApproveCommands: List<String> = plugin.config.getStringList("commands.approve")
                    if (!getApproveCommands.isEmpty()) {
                        getApproveCommands.forEach {
                            plugin.server.dispatchCommand(plugin.server.consoleSender, it.replace("#player#", checkedPlayer.player.name))
                        }
                    }

                })
                return@Runnable
            }
        }, 0L, 20L)
        val taskId = task.taskId

        clockTimerScheduler.put(checkedPlayer, taskId)
    }

    fun startChatMessageScheduler(checkedPlayer: CheckedPlayer) {
        val schedulerTimer: Long = plugin.config.getLong("settings.message-timer") * 20L
        val task = plugin.runTaskTimerAsynchronously(Runnable {
            if (!checkedPlayer.player.isOnline) return@Runnable

            val player: Player = checkedPlayer.player
            val inspector: Player? = checkedPlayer.inspector.player

            if (inspector == null || !inspector.isOnline) {
                checkedPlayer.disprove()
                return@Runnable
            }

            val playerAudience = player.getAudience()
            val inspectorName = inspector.name
            val hoverMessage: String? = plugin.getMessagesConfig().getString("chat.hover")
            if (checkedPlayer.hasTimer()) {
                val timer: Long = checkedPlayer.timer
                val secToMin: Long = TimeUnit.SECONDS.toMinutes(timer)
                val with_timer: List<String> = plugin.getMessagesConfig().getStringList("chat.with-timer")

                with_timer.forEach {
                    var message = it
                    message = message
                        .replace("#inspector#", inspectorName)
                        .replace("#minutes#", secToMin.toString())
                        .replace("#seconds#", timer.toString())

                    if(hoverMessage != null && !hoverMessage.isBlank()) playerAudience.multiMessage(message, hoverMessage, ClickEvent.Action.RUN_COMMAND, "/check confirm")
                    else playerAudience.multiMessage(message)
                }
            } else {
                val without_timer: List<String> = plugin.getMessagesConfig().getStringList("chat.without-timer")
                without_timer.forEach {
                    var message = it
                    message = message
                        .replace("#inspector#", inspectorName)

                    if(hoverMessage != null && !hoverMessage.isBlank()) playerAudience.multiMessage(message, hoverMessage, ClickEvent.Action.RUN_COMMAND, "/check confirm")
                    else playerAudience.multiMessage(message)
                }
            }
        }, 0L, schedulerTimer)
        val taskId = task.taskId

        messageChatTimer.put(checkedPlayer, taskId)
    }

    fun startScreenMessageScheduler(checkedPlayer: CheckedPlayer) {
        val player: Player = checkedPlayer.player
        val inspector: Player? = checkedPlayer.inspector.player
        if(inspector == null) return

        val task = plugin.runTaskTimer(Runnable {
            if (!checkedPlayer.player.isOnline) return@Runnable

            if (inspector == null || !inspector.isOnline) {
                checkedPlayer.disprove()
                return@Runnable
            }

            val timer: Long? = checkedPlayer.timer

            this.startTitle(player, timer)
            this.startActionbar(player, inspector, timer)
        }, 0L, 20L)
        val taskId = task.taskId

        messageScreenTimer.put(player, taskId)
        messageScreenTimer.put(inspector, taskId)
    }

    private fun startTitle(player: Player, timer: Long?) {
        if (timer != null) {
            val titleEnabled: Boolean = plugin.config.getBoolean("settings.title")
            if (titleEnabled) {
                val titleMessage: String = plugin.getMessagesConfig().getString("screen.check-title") !!
                val subTitleMessage: String = plugin.getMessagesConfig().getString("screen.check-subtitle") !!
                player.getAudience().multiTitle(titleMessage, subTitleMessage, 1, 15, 2)
            }
        }
    }

    private fun startBossBarScheduler(checkedPlayer: CheckedPlayer) {
        val bossBar = Bossbar(plugin, checkedPlayer)
        val task = plugin.runTaskTimer(Runnable {
            bossBar.show()
        }, 0L, 20L)
        val taskId = task.taskId

        bossbarScheduler.put(checkedPlayer.player, taskId)
        bossbarScheduler.put(checkedPlayer.inspector.player, taskId)
    }

    private fun startActionbar(player: Player, inspector: Player, timer: Long?) {
        if (timer != null) {
            val actionbarEnabled: Boolean = plugin.config.getBoolean("settings.actionbar")
            if (actionbarEnabled) {
                val message: String = plugin.getMessagesConfig().getString("screen.actionbar")?.replace("#seconds#", timer.toString())!!
                player.getAudience().multiActionbar(message)
                inspector.getAudience().multiActionbar(message)
            }
        }
    }

    fun stopTimer(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        clockTimerScheduler.remove(checkedPlayer)?.let { taskID ->
            plugin.server.scheduler.cancelTask(taskID)
        }
    }

    fun stopAll(checkedPlayer: CheckedPlayer) {
        stopMessages(checkedPlayer)

        if (clockTimerScheduler.get(checkedPlayer) != null) {
            checkedPlayer.stopTimer()
        }
    }

    private fun stopMessages(checkedPlayer: CheckedPlayer?) {
        if (checkedPlayer == null) return

        val scheduler = plugin.server.scheduler

        messageChatTimer.remove(checkedPlayer)?.let { scheduler.cancelTask(it) }
        messageScreenTimer.remove(checkedPlayer.player)?.let { scheduler.cancelTask(it) }
    }

    fun sendPunishmentCommand(player: Player, alias: String) {
        val console: ConsoleCommandSender = plugin.server.consoleSender
        val commands: List<String> = plugin.config.getStringList("commands.$alias")

        if(commands.isNotEmpty()) {
            commands.forEach {
                plugin.server.dispatchCommand(console, it.replace("#player#", player.name))
            }
        }
    }

}