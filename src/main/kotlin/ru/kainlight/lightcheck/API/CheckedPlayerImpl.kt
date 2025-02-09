package ru.kainlight.lightcheck.API

import org.bukkit.Location
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.events.PlayerApproveCheckEvent
import ru.kainlight.lightcheck.API.events.PlayerDisproveCheckEvent
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightcheck.UTILS.GroundLocation
import ru.kainlight.lightcheck.err
import ru.kainlight.lightcheck.warn
import ru.kainlight.lightlibrary.getAudience
import ru.kainlight.lightlibrary.sendMessage

internal data class CheckedPlayerImpl(
    override val player: Player,
    private val inspectorPlayer: Player,
    override var timer: Long = Main.getInstance().config.getLong("settings.timer"),
    override var hasTimer: Boolean = false,
    override val previousLocation: Location = player.location
) : CheckedPlayer {

    override val inspector: InspectorPlayer = InspectorPlayerImpl(inspectorPlayer, this, inspectorPlayer.location)

    override fun approve() {
        if (!player.isOnline || !LightCheckAPI.getProvider().isChecking(player)) return

        val event = PlayerApproveCheckEvent(player)
        Main.getInstance().server.pluginManager.callEvent(event)
        if (event.isCancelled) return

        player.getAudience().clearTitle()
        Main.getInstance().runnables.stopAll(this)
        LightCheckAPI.getProvider().getCheckedPlayers().remove(this)
        player.isInvulnerable = false

        LightCheckAPI.getProvider().getOccupiedLocations().remove(player)
    }

    override fun disprove() {
        if (!player.isOnline || !LightCheckAPI.getProvider().isChecking(player)) return

        val event = PlayerDisproveCheckEvent(player)
        Main.getInstance().server.pluginManager.callEvent(event)
        if (event.isCancelled) return

        player.getAudience().clearTitle()
        teleportToPreviousLocation()
        Main.getInstance().runnables.stopAll(this)
        LightCheckAPI.getProvider().getCheckedPlayers().remove(this)
        player.isInvulnerable = false

        LightCheckAPI.getProvider().getOccupiedLocations().remove(player)
    }

    override fun teleportToInspector() {
        if (!player.isOnline || !inspector.player.isOnline) return

        val teleportToStaffIsEnabled = Main.getInstance().config.getBoolean("abilities.teleport-to-inspector.enable", false)
        if (!teleportToStaffIsEnabled) return

        val groundLocation = GroundLocation(inspectorPlayer.location).getGroundLocation()
        player.teleport(groundLocation)
    }

    override fun teleportToCheckLocation(): Boolean {
        if (!player.isOnline || !inspector.player.isOnline) return false

        val teleportToLocations = Main.getInstance().config.getConfigurationSection("abilities.teleport-to-location")
        if (teleportToLocations == null) return true

        val isEnabled = teleportToLocations.getBoolean("enable", false)
        if (!isEnabled) return false

        val inspectorPlayer = inspectorPlayer.getAudience()

        val locationStrings = teleportToLocations.getStringList("locations")
        if (locationStrings.isEmpty()) {
            err("No locations defined in config")
            return false
        }

        val randomCachedLocations = LightCheckAPI.getProvider().getCachedCheckLocations()
        for (location in randomCachedLocations) {
            if (!LightCheckAPI.getProvider().getOccupiedLocations().values.contains(location)) {
                player.teleport(location)
                inspector.player.teleport(location)
                LightCheckAPI.getProvider().getOccupiedLocations().put(player, location)
                return true
            }
        }

        Main.getInstance().getMessagesConfig().getString("errors.no-available-locations")
            .sendMessage(inspectorPlayer)
        warn("No free locations available")
        return false
    }

    override fun teleportToPreviousLocation() {
        if (!player.isOnline) return

        val config = Main.getInstance().config
        val teleportToStaffEnabled = config.getBoolean("abilities.teleport-to-inspector.enable", false)
        val teleportFromStaff = teleportToStaffEnabled && config.getBoolean("abilities.teleport-to-inspector.after-back", false)

        val teleportToLocationsEnabled = config.getBoolean("abilities.teleport-to-location.enable", false)
        val teleportFromLocation = teleportToLocationsEnabled && config.getBoolean("abilities.teleport-to-location.after-back", false)

        // !
        when {
            teleportToLocationsEnabled && !teleportFromLocation -> player.teleport(player.world.spawnLocation)
            teleportFromStaff || teleportFromLocation -> player.teleport(previousLocation)
        }
    }

    override fun startTimer(): Boolean {
        return if (!hasTimer) {
            hasTimer = true
            Main.getInstance().runnables.startTimerScheduler(this)
            true
        } else false
    }

    override fun stopTimer(): Boolean {
        return if (hasTimer) {
            hasTimer = false
            Main.getInstance().runnables.stopTimer(this)
            true
        } else false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CheckedPlayer) return false
        return player.uniqueId == other.player.uniqueId &&
                inspector.player.uniqueId == other.inspector.player.uniqueId
    }

    override fun hashCode(): Int {
        var result = player.uniqueId.hashCode()
        result = 31 * result + inspector.player.uniqueId.hashCode()
        return result
    }

}
