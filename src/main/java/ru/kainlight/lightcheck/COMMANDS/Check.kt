package ru.kainlight.lightcheck.COMMANDS

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightcheck.UTILS.getAudience
import ru.kainlight.lightlibrary.equalsIgnoreCase
import ru.kainlight.lightlibrary.multiMessage
import ru.kainlight.lightlibrary.title

@Suppress("WARNINGS")
class Check(val plugin: Main) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            if (!(sender.hasPermission("lightcheck.check"))) {
                sender.noPermissionsMessage()
                return true
            }

            plugin.getMessageConfig().getStringList("help.commands").forEach {
                sender.getAudience().multiMessage(it)
            }
            return true
        }

        if (args.size == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("lightcheck.reload")) {
                sender.noPermissionsMessage()
                return true
            }

            plugin.saveDefaultConfig()
            plugin.messageConfig.saveDefaultConfig()
            plugin.reloadConfig()
            plugin.reloadParseMode()
            plugin.messageConfig.reloadConfig()
            plugin.messageConfig.reloadLanguage("language")

            plugin.getMessageConfig().getString("successfully.reload")?.let {
                sender.getAudience().multiMessage(it)
            }
            return true
        }

        if (sender !is Player) return true

        when(args[0].lowercase()) {
            "list" -> {
                if (!sender.hasPermission("lightcheck.list")) {
                    sender.noPermissionsMessage()
                    return true
                }

                val checkedPlayers = LightCheckAPI.get().checkedPlayers
                val checkedPlayersCount = checkedPlayers.size

                val header: String = plugin.getMessageConfig().getString("list.header")!!
                val text: String = plugin.getMessageConfig().getString("list.body")!!
                val footer: String = plugin.getMessageConfig().getString("list.footer")!!
                    .replace("#count#", checkedPlayersCount.toString())

                sender.getAudience().multiMessage(header)
                checkedPlayers.forEach{checked: CheckedPlayer ->
                    val message = text.replace("#inspector#", checked.inspector.name).replace("#username#", checked.player.name)
                    sender.getAudience().multiMessage(message)
                }
                sender.getAudience().multiMessage(footer)
                return true
            }
            "confirm" -> {
                if (! LightCheckAPI.get().isChecking(sender)) {
                    sender.noPermissionsMessage()
                    return true
                }

                val checkedPlayer = LightCheckAPI.get().getCheckedPlayer(sender)
                if (checkedPlayer.isEmpty) {
                    sender.noChecksMessage()
                    return true
                }
                val checked = checkedPlayer.get()

                val approve_player: String = plugin.getMessageConfig().getString("successfully.confirm")
                    ?.replace("#username#", sender.getName())!!
                checked.inspector.multiMessage(approve_player)

                checked.approve()
                plugin.runnables.sendPunishmentCommand(checked.player, "confirm")

                plugin.runnables.addLog(checked.player.name, approve_player)
                return true
            }
            "approve" -> {
                if (! sender.hasPermission("lightcheck.approve")) {
                    sender.noPermissionsMessage()
                    return true
                }

                val checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender)
                if (checkedPlayer.isEmpty) {
                    sender.noChecksMessage()
                    return true
                }

                val checked = checkedPlayer.get()

                val approve_staff: String = plugin.getMessageConfig().getString("successfully.approve")
                    ?.replace("#username#", checked.player.name)!!
                sender.getAudience().multiMessage(approve_staff)

                checked.approve()
                plugin.runnables.sendPunishmentCommand(checked.player, "approve")

                plugin.runnables.addLog(checked.player.name, approve_staff)
                return true
            }
            "disprove" -> {
                if (! (sender.hasPermission("lightcheck.disprove"))) {
                    sender.noPermissionsMessage()
                    return true
                }

                val checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender)
                if (checkedPlayer.isEmpty) {
                    sender.noChecksMessage()
                    return true
                }
                val checked = checkedPlayer.get()

                val disproved_for_staff: String =
                    plugin.getMessageConfig().getString("successfully.disprove.staff")
                        ?.replace("#username#", checked.player.name)!!
                sender.getAudience().multiMessage(disproved_for_staff)

                val titleEnabled = plugin.config.getBoolean("settings.title")
                if (titleEnabled) {
                    val titleMessage: String = plugin.getMessageConfig().getString("screen.disprove-title")!!
                    val subTitleMessage: String = plugin.getMessageConfig().getString("screen.disprove-subtitle")!!
                    checked.player.title(titleMessage, subTitleMessage, 1,3,1)
                }

                val disproved_for_player: String =
                    plugin.getMessageConfig().getString("successfully.disprove.player")!!
                checked.player.multiMessage(disproved_for_player)

                checked.disprove()
                plugin.runnables.sendPunishmentCommand(checked.player, "disprove")

                plugin.runnables.addLog(checked.player.name, disproved_for_staff)
                return true
            }
            "timer" -> {
                if (! sender.hasPermission("lightcheck.timer.continue") && ! sender.hasPermission("lightcheck.timer.stop")) {
                    sender.noPermissionsMessage()
                    return true
                }
                val checkedPlayer = LightCheckAPI.get().getCheckedPlayerByInspector(sender)
                if (checkedPlayer.isEmpty) {
                    sender.noChecksMessage()
                    return true
                }
                val checked = checkedPlayer.get()

                if (args[1].equalsIgnoreCase("continue") && sender.hasPermission("lightcheck.timer.continue") && !checked.hasTimer()) {
                    val started = checked.startTimer()

                    if (started) {
                        val message: String =
                            plugin.getMessageConfig().getString("successfully.timer.continue")
                                ?.replace("#username#", checked.player.name)
                                ?.replace("#value#", checked.timer.toString())!!
                        sender.getAudience().multiMessage(message)
                        plugin.runnables.addLog(checked.player.name, message)
                    }
                    return true
                }

                if (args[1].equals("stop", ignoreCase = true) && sender.hasPermission("lightcheck.timer.stop")) {
                    val stopped = checked.stopTimer()

                    if (stopped) {
                        val message: String = plugin.getMessageConfig().getString("successfully.timer.stop")
                            ?.replace("#username#", checked.player.name)!!
                        sender.getAudience().multiMessage(message)
                        plugin.runnables.addLog(checked.player.name, message)
                    }
                    return true
                }
                return true
            }
            "stopall", "stop-all" -> {
                if (! (sender.hasPermission("lightcheck.stop-all"))) {
                    sender.noPermissionsMessage()
                    return true
                }

                val stopall: String = plugin.getMessageConfig().getString("successfully.stop-all")!!
                sender.getAudience().multiMessage(stopall)

                LightCheckAPI.get().stopAll()
                return true
            }
            else -> {
                if (! sender.hasPermission("lightcheck.check")) {
                    sender.noPermissionsMessage()
                    return true
                }
                if (args.size != 1) return true

                val username = args[0]
                val offlinePlayer = plugin.server.getOfflinePlayer(username)
                val player = plugin.server.getPlayer(username)

                if (! offlinePlayer.hasPlayedBefore() && player == null) {
                    val notFound: String = plugin.getMessageConfig().getString("errors.not-found")!!
                    sender.getAudience().multiMessage(notFound)
                    return true
                } else if (offlinePlayer.hasPlayedBefore() && player == null) {
                    plugin.runnables.offlineChecks.putIfAbsent(offlinePlayer, sender.getName())

                    val offlineCall: String =
                        plugin.getMessageConfig().getString("successfully.offline-call")!!
                            .replace("#username#", username)
                    sender.getAudience().multiMessage(offlineCall)

                    plugin.runnables.addLog(offlinePlayer.name !!, offlineCall)
                    return true
                }

                if (player !!.hasPermission("lightcheck.bypass")) {
                    val already: String = plugin.getMessageConfig().getString("errors.bypass")!!
                        .replace("#username#", player !!.name)
                    sender.getAudience().multiMessage(already)
                    return true
                }

                if (player == sender) {
                    val call_self: String = plugin.getMessageConfig().getString("errors.call-self")!!
                    sender.getAudience().multiMessage(call_self)
                    return true
                }

                if (LightCheckAPI.get().isCheckingByInspector(sender)) {
                    val already_self: String = plugin.getMessageConfig().getString("errors.already-self")!!
                    sender.getAudience().multiMessage(already_self)
                    return true
                }

                val checkedPlayer = LightCheckAPI.get().getCheckedPlayer(player)
                if (checkedPlayer.isPresent) {
                    val already: String = plugin.getMessageConfig().getString("errors.already")!!
                    sender.getAudience().multiMessage(already)
                    return true
                }

                LightCheckAPI.get().call(player, sender)
                val call: String = plugin.getMessageConfig().getString("successfully.call")?.replace("#username#", username) !!
                sender.getAudience().multiMessage(call)

                plugin.runnables.addLog(player.name, call)
                return true
            }
        }
    }

    private fun CommandSender.noPermissionsMessage() {
        plugin.getMessageConfig().getString("errors.no-permissions")?.let {
            this.getAudience().multiMessage(it)
        }
    }
    private fun CommandSender.noChecksMessage() {
        plugin.getMessageConfig().getString("errors.no-checks")?.let {
            this.getAudience().multiMessage(it)
        }
    }

}