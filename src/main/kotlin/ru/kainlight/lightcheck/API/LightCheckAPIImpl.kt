package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.events.PlayerCheckEvent
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightcheck.info
import ru.kainlight.lightlibrary.LightConfig
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

internal class LightCheckAPIImpl(private val plugin: Main) : LightCheckAPI {

    init {
        info("API is loaded")
    }

    private val legacyColorsRegex = """[&][0-9a-fk-or]""".toRegex()
    private val minimessageRegex = """<[^>]+>""".toRegex()

    private val checkedPlayers: MutableSet<CheckedPlayer> = mutableSetOf()
    private val cachedCheckLocations: MutableList<Location> = CopyOnWriteArrayList()
    private val occupiedLocations: MutableMap<Player, Location> = mutableMapOf()

    override fun call(player: Player?, inspector: Player?): Boolean {
        if(player == null || inspector == null) return false

        val event = PlayerCheckEvent(player)
        plugin.server.pluginManager.callEvent(event)
        if (event.isCancelled) return false

        val checkedPlayer = CheckedPlayerImpl(player, inspector)
        checkedPlayers.add(checkedPlayer)

        player.isInvulnerable = true

        if (!checkedPlayer.teleportToCheckLocation()) {
            checkedPlayer.teleportToInspector()
        }

        plugin.runnables.start(checkedPlayer)
        return true
    }

    // $ DEV-7
    // TODO: Добавление в логи перенесено, и в API добавлено. Need Test
    override fun addLog(username: String, text: String): String? {
        val enabled: Boolean = plugin.config.getBoolean("settings.logging", false)
        if (!enabled) return null

        val cleanedText = text
            .replace(legacyColorsRegex, "")
            .replace(minimessageRegex, "")

        // !
        val logsFolder = File(plugin.dataFolder, "logs")
        if (!logsFolder.exists()) logsFolder.mkdirs()

        val logFile = File(logsFolder, "$username.yml")
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
        // !

        val logConfig = LightConfig(plugin, "logs", "$username.yml")
        val list: MutableList<String> = logConfig.getConfig().getStringList("log")
        list.add(cleanedText)

        logConfig.getConfig().set("log", list)
        logConfig.saveConfig()

        return cleanedText
    }

    override fun isChecking(player: Player?): Boolean {
        return if(player == null) false else getCheckedPlayer(player) != null
    }

    override fun isCheckingByInspector(inspector: Player?): Boolean {
        return if(inspector == null) false else getCheckedPlayerByInspector(inspector) != null
    }

    override fun getCheckedPlayer(player: Player?): CheckedPlayer? {
        return checkedPlayers.firstOrNull { it.player == player }
    }

    override fun getInspectorByPlayer(player: Player?): InspectorPlayer? {
        return getCheckedPlayer(player)?.inspector
    }

    override fun getCheckedPlayerByInspector(inspector: Player?): CheckedPlayer? {
        return checkedPlayers.firstOrNull { it.inspector.player == inspector }
    }

    override fun getCheckedPlayers(): MutableSet<CheckedPlayer> {
        return this.checkedPlayers
    }

    override fun getInspectors(): List<InspectorPlayer> {
        return getCheckedPlayers().map { it.inspector }
    }

    override fun getCachedCheckLocations(): MutableList<Location> {
        return this.cachedCheckLocations
    }
    override fun getOccupiedLocations(): MutableMap<Player, Location> {
        return this.occupiedLocations
    }

    override fun stopAll(): List<CheckedPlayer> {
        val stoppedChecksList: MutableList<CheckedPlayer> = mutableListOf()

        plugin.server.onlinePlayers.forEach { online ->
            getCheckedPlayer(online)?.let {
                it.disprove()
                stoppedChecksList.add(it)
            }
        }

        return stoppedChecksList.toList()
    }
}