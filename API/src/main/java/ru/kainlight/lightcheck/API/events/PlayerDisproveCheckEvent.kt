package ru.kainlight.lightcheck.API.events

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.InspectorPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI

/**
 * Event that is triggered when a player is disproved during a check.
 */
@Suppress("UNUSED")
class PlayerDisproveCheckEvent(player: Player) : PlayerEvent(player), Cancellable {

    val provider = LightCheckAPI.getProvider()

    /**
     * Gets the checked player instance for the event's player.
     *
     * @return The checked player instance, or `null` if not found.
     */
    val checkedPlayer: CheckedPlayer? = LightCheckAPI.getProvider().getCheckedPlayer(player)
    val inspector = checkedPlayer?.inspector

    private var isCancelled = false

    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        private val handlerList: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }
    }
}