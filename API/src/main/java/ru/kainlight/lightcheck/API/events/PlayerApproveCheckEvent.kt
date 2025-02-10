package ru.kainlight.lightcheck.API.events

import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

/**
 * Event that is triggered when a player is approved during a check.
 */
@Suppress("UNUSED")
class PlayerApproveCheckEvent(
    private val player: Player,
    private val checkedPlayer: CheckedPlayer = LightCheckAPI.getProvider().getCheckedPlayer(player)!!
) : PlayerEvent(player), Cancellable {

    private var isCancelled = false

    /**
     * Gets the checked player instance for the event's player.
     *
     * @return The checked player instance, or `null` if not found.
     */
    fun getCheckedPlayer(): CheckedPlayer? {
        return checkedPlayer
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    override fun isCancelled(): Boolean {
        return this.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        this.isCancelled = cancel
    }

    fun cancel() {
        this.isCancelled = true
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