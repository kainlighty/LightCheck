package ru.kainlight.lightcheck.EVENTS

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.multiMessage
import java.util.*

class CheckedListener(val plugin: Main) : Listener {

    @EventHandler
    fun onJoinChecked(event: PlayerJoinEvent) {
        val player = event.getPlayer();
        val inspectorName: String? = plugin.runnables.offlineChecks.get(player)

        if (inspectorName != null) {
            val inspector: Player? = plugin.getServer().getPlayer(inspectorName)

            if (inspector != null) LightCheckAPI.get().call(player, inspector)
            plugin.runnables.offlineChecks.remove(player)
        }

    }

    @EventHandler
    fun onQuitChecked(event: PlayerQuitEvent) {
        val player = event.getPlayer();
        val checkedPlayer: Optional<CheckedPlayer> = LightCheckAPI.get().getCheckedPlayer(player);

        checkedPlayer.ifPresent { checked ->
            checked.approve();
            plugin.runnables.sendPunishmentCommand(checked.player, "quit")
        }

    }

    @EventHandler
    fun onKickChecked(event: PlayerKickEvent) {
        val player = event.player;
        val checkedPlayer: Optional<CheckedPlayer> = LightCheckAPI.get().getCheckedPlayer(player)

        if (checkedPlayer.isPresent) {
            val checked: CheckedPlayer = checkedPlayer.get()
            checked.approve()

            val timer: Long? = checked.timer
            if (timer != null && timer >= 0) {
                plugin.runnables.sendPunishmentCommand(checked.getPlayer(), "kick")
            }
        }
    }

    @EventHandler
    fun onCommandsChecked(event: PlayerCommandPreprocessEvent) {
        val player: Player = event.player;

        if (isCheckingAndAbilityEnabled(player, "block-chat.enable")) {
            val allowedCommands: MutableList<String> = plugin.getConfig().getStringList("abilities.block-chat.allowed-commands")
            val message: List<String> = event.message.replace("/", "").split(" ")

            if (! allowedCommands.contains(message[0])) {
                event.isCancelled = true
            }
        }
    }


    @EventHandler
    fun onChatChecked(event: AsyncPlayerChatEvent) {
        val player: Player = event.player

        if (isCheckingAndAbilityEnabled(player, "block-chat.enable")) {
            event.isCancelled = true

            val staff: Player = LightCheckAPI.get().getCheckedPlayer(player).get().getInspector()
            val privateDialog: String = plugin.getMessageConfig().getString("chat.dialog")
                ?.replace("#username#", player.name)
                ?.replace("#message#", event.message)!!

            this.privateMessage(privateDialog, player, staff)
        }
    }

    @EventHandler
    fun onMoveChecked(event: PlayerMoveEvent) {
        val player: Player = event.player

        if (isCheckingAndAbilityEnabled(player, "block-move")) {
            event.to = event.from
        }
    }

    @EventHandler
    fun onDropItemsChecked(event: PlayerDropItemEvent) {
        val player: Player = event.player;

        if (isCheckingAndAbilityEnabled(player, "block-drops")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickupItemsChecked(event: EntityPickupItemEvent) {
        val entity = event.entity
        if (entity is Player) {
            if (isCheckingAndAbilityEnabled(entity, "block-pickup")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamageChecked(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity is Player) {
            if (isCheckingAndAbilityEnabled(entity, "block-damage")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamageChecked(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (entity is Player) {
            if (isCheckingAndAbilityEnabled(entity, "block-damage")) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onInteractChecked(event: PlayerInteractEvent) {
        val player: Player = event.player;

        if (isCheckingAndAbilityEnabled(player, "block-interact")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onGamemodeChangeChecked(event: PlayerGameModeChangeEvent) {
        val player: Player = event.player;

        if (isCheckingAndAbilityEnabled(player, "block-gamemode")) {
            event.isCancelled = true
        }
    }

    private fun isCheckingAndAbilityEnabled(player: Player, abilityName: String): Boolean {
        val isChecked: Boolean = LightCheckAPI.get().isChecking(player);
        val abilityEnabled: Boolean = plugin.config.getBoolean("abilities.$abilityName")
        return isChecked && abilityEnabled
    }

    @SafeVarargs
    private fun privateMessage(text: String, vararg players: Player){
        players.forEach { it.multiMessage(text) }
    }

}