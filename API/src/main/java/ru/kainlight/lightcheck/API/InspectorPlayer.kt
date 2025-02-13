package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Interface for managing an inspector player.
 */
interface InspectorPlayer {

    /**
     * Gets the player who is acting as an inspector.
     *
     * @return The player who is acting as an inspector.
     */
    val player: Player

    /**
     * Gets the player who is being checked by this inspector.
     *
     * @return The player who is being checked.
     */
    val checkedPlayer: CheckedPlayer

    /**
     * Sets or gets the previous location of the inspector.
     *
     * @return The previous location of the inspector.
     */
    val previousLocation: Location

    /**
     * Teleports the inspector to their previous location.
     *
     * This method moves the inspector back to their previous location.
     */
    fun teleportToPreviousLocation()
}