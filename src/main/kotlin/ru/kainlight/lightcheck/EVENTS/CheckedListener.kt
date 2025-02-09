package ru.kainlight.lightcheck.EVENTS

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.multiMessage

internal class CheckedListener(private val plugin: Main) : Listener {

    @EventHandler
    fun onJoinChecked(event: PlayerJoinEvent) {
        val player = event.getPlayer()

        plugin.runnables.offlineChecks.get(player)?.let { inspectorName ->
            val inspector: Player? = plugin.server.getPlayer(inspectorName)

            if (inspector != null) LightCheckAPI.getProvider().call(player, inspector)
            plugin.runnables.offlineChecks.remove(player)
        }
    }

    @EventHandler
    fun onQuitChecked(event: PlayerQuitEvent) {
        LightCheckAPI.getProvider().getCheckedPlayer(event.player)?.let { checked ->
            checked.approve()
            plugin.runnables.sendPunishmentCommand(checked.player, "quit")
        }

    }

    @EventHandler
    fun onKickChecked(event: PlayerKickEvent) {
        LightCheckAPI.getProvider().getCheckedPlayer(event.player)?.let { checked -> {
            checked.approve()

            val timer: Long? = checked.timer
            if (timer != null && timer >= 0) {
                plugin.runnables.sendPunishmentCommand(checked.player, "kick")
            }
        } }
    }

    @EventHandler
    fun onCommandsChecked(event: PlayerCommandPreprocessEvent) {
        if (event.player.isCheckingAndAbilityEnabled("block-chat.enable")) {
            val allowedCommands: MutableList<String> =
                plugin.getConfig().getStringList("abilities.block-chat.allowed-commands")
            val message: List<String> = event.message.replace("/", "").split(" ")

            if (! allowedCommands.contains(message[0])) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onChatChecked(event: AsyncPlayerChatEvent) {
        val player: Player = event.player

        if (player.isCheckingAndAbilityEnabled("block-chat.enable")) {
            event.isCancelled = true

            val staff: Player? = LightCheckAPI.getProvider().getCheckedPlayer(player)?.inspector?.player
            val privateDialog: String = plugin.getMessagesConfig().getString("chat.dialog")
                ?.replace("#username#", player.name)
                ?.replace("#message#", event.message) !!

            this.privateMessage(privateDialog, player, staff)
        }
    }

    @EventHandler
    fun onMoveChecked(event: PlayerMoveEvent) {
        if (event.player.isCheckingAndAbilityEnabled("block-move")) {
            event.to = event.from
        }
    }

    @EventHandler
    fun onDropItemsChecked(event: PlayerDropItemEvent) {
        if (event.player.isCheckingAndAbilityEnabled("block-drops")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickupItemsChecked(event: EntityPickupItemEvent) {
        val player = event.entity
        if (player is Player) {
            if (player.isCheckingAndAbilityEnabled("block-pickup")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamageChecked(event: EntityDamageEvent) {
        val player = event.entity
        if (player is Player) {
            if (player.isCheckingAndAbilityEnabled("block-damage")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamageChecked(event: EntityDamageByEntityEvent) {
        val player = event.entity
        if (player is Player) {
            if (player.isCheckingAndAbilityEnabled("block-damage")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInteractChecked(event: PlayerInteractEvent) {
        if (event.player.isCheckingAndAbilityEnabled("block-interact")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onGamemodeChangeChecked(event: PlayerGameModeChangeEvent) {
        if (event.player.isCheckingAndAbilityEnabled("block-gamemode")) {
            event.isCancelled = true
        }
    }

    private fun Player.isCheckingAndAbilityEnabled(abilityName: String): Boolean {
        val isChecked: Boolean = LightCheckAPI.getProvider().isChecking(player!!)
        val abilityEnabled: Boolean = plugin.config.getBoolean("abilities.$abilityName")
        return isChecked && abilityEnabled
    }

    @SafeVarargs
    private fun privateMessage(text: String, vararg players: Player?) {
        players.forEach { it?.multiMessage(text) }
    }

}