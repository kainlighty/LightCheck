package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Interface for managing a checked player.
 */
interface CheckedPlayer {

    /**
     * Gets the player under check.
     *
     * @return The player under check.
     */
    val player: Player

    /**
     * Sets or gets the time (in seconds) during which the player is under check.
     *
     * @return Time in seconds.
     */
    var timer: Long

    /**
     * Gets the last known location of the player.
     *
     * @return The location of the player.
     */
    val previousLocation: Location

    /**
     * Gets the object representing the inspector conducting the check.
     *
     * @return An object of type InspectorPlayer.
     */
    val inspector: InspectorPlayer

    /**
     * Approves the result of the check.
     *
     * This method is usually called when the player agrees with the result of the check.
     */
    fun approve()

    /**
     * Disapproves the result of the check.
     *
     * This method is usually called when the player disagrees with the result of the check.
     */
    fun disprove()

    /**
     * Teleports the player to the inspector's location.
     *
     * This method moves the player to the location where the inspector is located.
     */
    fun teleportToInspector()

    /**
     * Teleports the player to the location where the check began.
     *
     * This method moves the player back to the location where the check began.
     *
     * @return `true` if the teleportation was successful; `false` otherwise.
     */
    fun teleportToCheckLocation(): Boolean

    /**
     * Teleports the player back to their previous location.
     *
     * This method moves the player back to their location before the check began.
     */
    fun teleportToPreviousLocation()

    /**
     * Starts the timer for the player.
     *
     * This method starts the timer for the player, which will count down the time of the check.
     *
     * @return `true` if the timer was successfully started; `false` otherwise.
     */
    fun startTimer(): Boolean
    /**
     * Is there a flag indicating if the player has a timer?
     *
     * @return `true` if the timer is active; `false` otherwise.
     */
    fun hasTimer(): Boolean

    /**
     * Stops the timer for the player.
     *
     * This method stops the timer for the player if it was running.
     *
     * @return `true` if the timer was successfully stopped; `false` otherwise.
     */
    fun stopTimer(): Boolean
}