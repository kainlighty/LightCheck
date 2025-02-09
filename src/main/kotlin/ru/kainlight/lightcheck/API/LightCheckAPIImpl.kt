package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.events.PlayerCheckEvent
import ru.kainlight.lightcheck.Main
import java.util.concurrent.CopyOnWriteArrayList

internal class LightCheckAPIImpl() : LightCheckAPI {

    private val checkedPlayers: MutableSet<CheckedPlayer> = mutableSetOf()

    private val cachedCheckLocations: MutableList<Location> = CopyOnWriteArrayList()
    private val occupiedLocations: MutableMap<Player, Location> = mutableMapOf()

    override fun call(player: Player?, inspector: Player?) {
        if(player == null || inspector == null) return

        val event = PlayerCheckEvent(player)
        Main.getInstance().server.pluginManager.callEvent(event)
        if (event.isCancelled) return

        val checkedPlayer = CheckedPlayerImpl(player, inspector)
        checkedPlayers.add(checkedPlayer)

        player.isInvulnerable = true

        if (!checkedPlayer.teleportToCheckLocation()) {


            checkedPlayer.teleportToInspector()
        }

        Main.getInstance().runnables.start(checkedPlayer)
    }

    override fun isChecking(player: Player?): Boolean {
        return if(player == null) false else getCheckedPlayer(player) != null
    }

    override fun isCheckingByInspector(inspector: Player?): Boolean {
        return if(inspector == null) false else getCheckedPlayerByInspector(inspector) != null
    }

    override fun getCheckedPlayer(player: Player): CheckedPlayer? {
        return checkedPlayers.firstOrNull { it.player == player }
    }

    override fun getCheckedPlayerByInspector(inspector: Player): CheckedPlayer? {
        return checkedPlayers.firstOrNull { it.inspector.player == inspector }
    }

    override fun getCheckedPlayers(): MutableSet<CheckedPlayer> {
        return this.checkedPlayers
    }
    override fun getCachedCheckLocations(): MutableList<Location> {
        return this.cachedCheckLocations
    }
    override fun getOccupiedLocations(): MutableMap<Player, Location> {
        return this.occupiedLocations
    }

    override fun stopAll() {
        Main.getInstance().server.onlinePlayers.forEach { online ->
            getCheckedPlayer(online)?.disprove()
        }
    }
}