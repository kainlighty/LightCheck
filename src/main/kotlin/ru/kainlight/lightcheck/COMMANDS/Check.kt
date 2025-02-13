package ru.kainlight.lightcheck.COMMANDS

import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightcheck.UTILS.GroundLocation
import ru.kainlight.lightlibrary.equalsIgnoreCase
import ru.kainlight.lightlibrary.getAudience
import ru.kainlight.lightlibrary.multiMessage
import ru.kainlight.lightlibrary.sendMessage
import ru.kainlight.lightlibrary.title

internal class Check(private val plugin: Main) : CommandExecutor {

    private val unsafeLocations: MutableMap<Player, Location> = mutableMapOf()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            if(sender.hasNoPermission("check")) return true

            plugin.getMessagesConfig().getStringList("help.commands").forEach {
                sender.getAudience().multiMessage(it)
            }
            return true
        }

        val senderAudience = sender.getAudience()

        if (args.size == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasNoPermission("reload")) return true

            plugin.reloadConfigurations()

            plugin.getMessagesConfig().getString("successfully.reload")?.let {
                senderAudience.multiMessage(it)
            }
            return true
        }

        if (sender !is Player) return true

        when (args[0].lowercase()) {
            "help" -> {
                if(sender.hasNoPermission("check")) return true

                plugin.getMessagesConfig().getStringList("help.commands").forEach {
                    senderAudience.multiMessage(it)
                }
                return true
            }
            "list" -> {
                if (sender.hasNoPermission("list")) return true

                val checkedPlayers = LightCheckAPI.getProvider().getCheckedPlayers()
                val checkedPlayersCount = checkedPlayers.size

                val header: String = plugin.getMessagesConfig().getString("list.header") !!
                val text: String = plugin.getMessagesConfig().getString("list.body") !!
                val footer: String = plugin.getMessagesConfig().getString("list.footer") !!
                    .replace("#count#", checkedPlayersCount.toString())

                senderAudience.multiMessage(header)
                checkedPlayers.forEach { checked: CheckedPlayer ->
                    val checkedName = checked.player.name
                    val inspectorName = checked.inspector.player.name

                    text.replace("#inspector#", inspectorName)
                        .replace("#player#", checkedName)
                        .sendMessage(senderAudience)
                }
                senderAudience.multiMessage(footer)
                return true
            }

            "confirm" -> {
                if (! LightCheckAPI.getProvider().isChecking(sender)) {
                    sender.noChecksMessage()
                    return true
                }

                val checked = LightCheckAPI.getProvider().getCheckedPlayer(sender)
                if (checked == null) {
                    sender.noChecksMessage()
                    return true
                }
                val checkedPlayer = checked.player

                plugin.getMessagesConfig().getString("successfully.confirm")
                    ?.replace("#username#", sender.name)?.let {
                        checked.inspector.player.getAudience().multiMessage(it)
                        LightCheckAPI.getProvider().addLog(checkedPlayer.name, it)
                    }

                checked.approve()
                plugin.runnables.sendPunishmentCommand(checkedPlayer, "confirm")
                return true
            }

            "approve" -> {
                if (sender.hasNoPermission("lightcheck.approve")) return true

                val checked = LightCheckAPI.getProvider().getCheckedPlayerByInspector(sender)
                if (checked == null) {
                    sender.noChecksMessage()
                    return true
                }
                val checkedPlayer = checked.player

                plugin.getMessagesConfig().getString("successfully.approve")
                    ?.replace("#username#", checkedPlayer.name)?.let {
                        senderAudience.multiMessage(it)
                        LightCheckAPI.getProvider().addLog(checkedPlayer.name, it)
                    }

                checked.approve()
                plugin.runnables.sendPunishmentCommand(checkedPlayer, "approve")
                return true
            }

            "disprove" -> {
                if (sender.hasNoPermission("disprove")) return true

                val checked = LightCheckAPI.getProvider().getCheckedPlayerByInspector(sender)
                if (checked == null) {
                    sender.noChecksMessage()
                    return true
                }
                val checkedPlayer = checked.player

                plugin.getMessagesConfig().getString("successfully.disprove.staff")
                    ?.replace("#username#", checkedPlayer.name)?.let {
                        senderAudience.multiMessage(it)
                    }

                val titleEnabled = plugin.config.getBoolean("settings.title")
                if (titleEnabled) {
                    val titleMessage: String = plugin.getMessagesConfig().getString("screen.disprove-title") !!
                    val subTitleMessage: String = plugin.getMessagesConfig().getString("screen.disprove-subtitle") !!
                    checkedPlayer.getAudience().title(titleMessage, subTitleMessage, 1, 3, 1)
                }

                plugin.getMessagesConfig().getString("successfully.disprove.player")?.let {
                    checkedPlayer.getAudience().multiMessage(it)
                    LightCheckAPI.getProvider().addLog(checkedPlayer.name, it)
                }

                checked.disprove()
                plugin.runnables.sendPunishmentCommand(checkedPlayer, "disprove")
                return true
            }

            "timer" -> {
                if (sender.hasNoPermission("timer.continue") || sender.hasNoPermission("timer.stop")) return true

                val checked = LightCheckAPI.getProvider().getCheckedPlayerByInspector(sender)
                if (checked == null) {
                    sender.noChecksMessage()
                    return true
                }
                val checkedPlayer = checked.player

                if (args[1].equalsIgnoreCase("continue") && sender.hasPermission("lightcheck.timer.continue") && ! checked.hasTimer()) {
                    val started = checked.startTimer()

                    if (started) {
                        plugin.getMessagesConfig().getString("successfully.timer.continue")
                            ?.replace("#username#", checkedPlayer.name)
                            ?.replace("#value#", checked.timer.toString())?.let {
                                senderAudience.multiMessage(it)
                                LightCheckAPI.getProvider().addLog(checkedPlayer.name, it)
                            }
                    }
                    return true
                }

                if (args[1].equals("stop", ignoreCase = true) && sender.hasPermission("lightcheck.timer.stop")) {
                    if (checked.stopTimer()) {
                        plugin.getMessagesConfig().getString("successfully.timer.stop")
                            ?.replace("#username#", checked.player.name)?.let {
                                senderAudience.multiMessage(it)
                                LightCheckAPI.getProvider().addLog(checked.player.name, it)
                            }
                    }
                    return true
                }
                return true
            }

            "stopall", "stop-all" -> {
                if (sender.hasNoPermission("stop-all")) return true

                plugin.getMessagesConfig().getString("successfully.stop-all")
                    ?.sendMessage(senderAudience)

                LightCheckAPI.getProvider().stopAll()
                return true
            }

            else -> {
                if (sender.hasNoPermission("check")) return true
                if (args.size != 1) return true

                val username = args[0]
                val offlinePlayer = plugin.server.getOfflinePlayer(username)
                val player = offlinePlayer.player

                if (! offlinePlayer.hasPlayedBefore() && player == null) {
                    plugin.getMessagesConfig().getString("errors.not-found")?.sendMessage(senderAudience)
                    return true
                } else if (offlinePlayer.hasPlayedBefore() && player == null) {
                    plugin.runnables.offlineChecks.putIfAbsent(offlinePlayer, sender.name)

                    plugin.getMessagesConfig().getString("successfully.offline-call")?.replace("#username#", username)
                        ?.let {
                            senderAudience.multiMessage(it)
                            LightCheckAPI.getProvider().addLog(offlinePlayer.name !!, it)
                        }
                    return true
                }

                if (player!!.hasPermission("lightcheck.bypass")) {
                    plugin.getMessagesConfig().getString("errors.bypass")
                        ?.replace("#username#", player.name)
                        ?.sendMessage(senderAudience)
                    return true
                }

                if (player == sender) {
                    plugin.getMessagesConfig().getString("errors.call-self")
                        ?.sendMessage(senderAudience)
                    return true
                }

                if (LightCheckAPI.getProvider().isCheckingByInspector(sender)) {
                    plugin.getMessagesConfig().getString("errors.already-self")
                        ?.sendMessage(senderAudience)
                    return true
                }

                if (LightCheckAPI.getProvider().isChecking(player)) {
                    plugin.getMessagesConfig().getString("errors.already")
                        ?.sendMessage(senderAudience)
                }

                val senderLocation = sender.location

                // Проверяем небезопасную зону
                if (! GroundLocation(senderLocation).isSafeForTeleport()) {
                    if (! unsafeLocations.containsKey(sender)) {
                        unsafeLocations.put(sender, senderLocation)
                        sender.unsafeLocationMessage(player.name)
                        return true
                    } else {
                        // Повторный вызов команды: считаем согласием на небезопасную зону
                        unsafeLocations.remove(sender)
                    }
                }

                if(LightCheckAPI.getProvider().call(player, sender)) {
                    val call: String = plugin.getMessagesConfig().getString("successfully.call")?.replace("#username#", username) !!
                    senderAudience.multiMessage(call)

                    LightCheckAPI.getProvider().addLog(player.name, call)
                }

                return true
            }
        }
    }

    private fun CommandSender.hasNoPermission(permission: String): Boolean {
        val b = this.hasPermission("lightcheck.$permission")

        if (! b) {
            plugin.getMessagesConfig().getString("errors.no-permissions")?.let {
                this.getAudience().multiMessage(it.replace("#permission#", permission))
            }
            return true
        } else return false
    }

    private fun CommandSender.noChecksMessage() {
        plugin.getMessagesConfig().getString("errors.no-checks")?.let {
            this.getAudience().multiMessage(it)
        }
    }

    private fun CommandSender.unsafeLocationMessage(checkedPlayerName: String) {
        this.getAudience().multiMessage(
            message = "Это небезопасная зона для телепортации. Для продолжения введите команду повторно или нажмите на это сообщение",
            event = ClickEvent.Action.RUN_COMMAND,
            action = "/lightcheck $checkedPlayerName"
        )
    }

}