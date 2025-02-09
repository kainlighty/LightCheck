package ru.kainlight.lightcheck.COMMANDS

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.equalsIgnoreCase

internal class Completer(private val plugin: Main) : TabCompleter {

    private val defaultCompletions = mutableListOf("continue", "stop")

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<String>): MutableList<String>? {
        if (!sender.hasPermission("lightcheck.check")) return null

        if (command.name.equalsIgnoreCase("lightcheck") || command.name.equalsIgnoreCase("check")) {
            if (args.size == 1) {
                val completionsCopy: MutableList<String> = mutableListOf("list", "approve", "disprove", "timer", "stop-all")
                val playerNames: List<String> = plugin.server.onlinePlayers.map { player: Player -> player.name }.toList()
                completionsCopy.addAll(playerNames)

                return completionsCopy
            } else if (args.size == 2 && args[0].equalsIgnoreCase("timer")) {
                return defaultCompletions
            }
        }
        return null
    }

}