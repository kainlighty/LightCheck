package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player

internal data class InspectorPlayerImpl(override val player: Player, override val checkedPlayer: CheckedPlayer, override var previousLocation: Location) : InspectorPlayer {

    override fun teleportToPreviousLocation() {
        player.teleport(previousLocation)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InspectorPlayer) return false
        return player.uniqueId == other.player.uniqueId &&
                checkedPlayer.player.uniqueId == other.checkedPlayer.player.uniqueId
    }

    override fun hashCode(): Int {
        var result = player.uniqueId.hashCode()
        result = 31 * result + checkedPlayer.player.uniqueId.hashCode()
        return result
    }

}