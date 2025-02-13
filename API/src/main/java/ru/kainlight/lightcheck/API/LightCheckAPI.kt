package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.annotations.ApiStatus
import ru.kainlight.lightcheck.API.exceptions.ProviderException

/**
 * Main API interface for LightCheck functionality.
 */
interface LightCheckAPI {

    companion object {
        private var provider: LightCheckAPI? = null

        @ApiStatus.Internal
        fun setProvider(value: LightCheckAPI) {
            if(provider == null) provider = value
            else throw ProviderException("The provider has already been assigned")
        }

        /**
         * Gets the provider instance.
         *
         * @return The provider instance.
         * @throws ProviderException If the provider is not assigned by the parent plugin.
         */
        @JvmStatic
        fun getProvider(): LightCheckAPI {
            return provider ?: throw ProviderException("The provider is not assigned by the parent plugin")
        }

        @JvmStatic
        fun removeProvider() {
            provider = null
        }
    }

    /**
     * Starts the check process for the specified player and inspector.
     *
     * @param player The player to be checked.
     * @param inspector The inspector conducting the check.
     *
     * @return `true` if the player is not null or event is not cancelled; `false` otherwise.
     */
    fun call(player: Player?, inspector: Player?): Boolean

    /**
     * Adds the specified message for the specified player to the log file (<username>.yml).
     *
     * @param username The player name.
     * @param text The message text.
     *
     * @return `null` if `settings.logging` = false; `text` otherwise.
     */
    fun addLog(username: String, text: String): String?

    /**
     * Checks if a player is currently being checked.
     *
     * @param player The player to check.
     * @return `true` if the player is being checked; `false` otherwise.
     */
    fun isChecking(player: Player?): Boolean

    /**
     * Checks if an inspector is currently conducting a check.
     *
     * @param inspector The inspector to check.
     * @return `true` if the inspector is conducting a check; `false` otherwise.
     */
    fun isCheckingByInspector(inspector: Player?): Boolean

    /**
     * Gets the checked player instance for the specified player.
     *
     * @param player The player to get the checked player for.
     * @return The checked player instance, or `null` if not found.
     */
    fun getCheckedPlayer(player: Player?): CheckedPlayer?

    /**
     * Gets the checked player instance for the specified inspector.
     *
     * @param inspector The inspector to get the checked player for.
     * @return The checked player instance, or `null` if not found.
     */
    fun getCheckedPlayerByInspector(inspector: Player?): CheckedPlayer?

    /**
     * Gets all the currently checked players.
     *
     * @return A mutable set containing all the checked players.
     */
    fun getCheckedPlayers(): MutableSet<CheckedPlayer>

    /**
     * Gets the inspector by (checked) player instance.
     *
     * @param player The player to get the checked player for.
     * @return The **InspectorPlayer** instance, or `null` if not found.
     */
    fun getInspectorByPlayer(player: Player?): InspectorPlayer?

    /**
     * Gets all the currently inspectors.
     *
     * @return The immutable list containing all the checked players.
     */
    fun getInspectors(): List<InspectorPlayer>

    /**
     * Gets the cached check locations.
     *
     * @return A mutable list containing the cached check locations.
     */
    fun getCachedCheckLocations(): MutableList<Location>

    /**
     * Gets the occupied locations by player.
     *
     * @return A mutable map containing players and their occupied locations.
     */
    fun getOccupiedLocations(): MutableMap<Player, Location>

    /**
     * Stops all ongoing checks.
     *
     * @return The immutable list of players for whom the check was stopped.
     */
    fun stopAll(): List<CheckedPlayer>
}

