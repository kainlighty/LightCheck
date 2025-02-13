package ru.kainlight.lightcheck

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.HandlerList
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.API.LightCheckAPIImpl
import ru.kainlight.lightcheck.COMMANDS.Check
import ru.kainlight.lightcheck.COMMANDS.Completer
import ru.kainlight.lightcheck.EVENTS.CheckedListener
import ru.kainlight.lightcheck.UTILS.Runnables
import ru.kainlight.lightlibrary.LightConfig
import ru.kainlight.lightlibrary.LightPlugin
import ru.kainlight.lightlibrary.UTILS.Init
import ru.kainlight.lightlibrary.UTILS.Parser

class Main : LightPlugin() {

    internal var debug = false
    internal lateinit var runnables: Runnables

    override fun onLoad() {
        this.saveDefaultConfig()
        configurationVersion = 1.5
        updateConfig()

        LightConfig.saveLanguages(this, "language")
        messageConfig.configurationVersion = 1.5
        messageConfig.updateConfig()
    }

    override fun onEnable() {
        instance = this

        this.enable()

        LightCheckAPI.setProvider(LightCheckAPIImpl(this))

        this.reloadConfigurations()

        this.runnables = Runnables(this)

        this.registerCommand("lightcheck", Check(this), Completer(this))
        this.registerListener(CheckedListener(this))

        Init.start(this, true)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        this.server.scheduler.cancelTasks(this)

        LightCheckAPI.removeProvider()
        Init.stop(this)
    }

    fun reloadConfigurations() {
        this.saveDefaultConfig()
        Parser.parseMode = this.config.getString("settings.parse_mode", "MINIMESSAGE") !!
        this.debug = this.config.getBoolean("debug", false)
        this.loadRandomLocations()
        this.reloadConfig()

        this.messageConfig.saveDefaultConfig()
        this.messageConfig.reloadConfig()
        this.messageConfig.reloadLanguage("language")
    }

    private fun loadRandomLocations() {
        val locations = this.config.getStringList("abilities.teleport-to-location.locations")

        for (locationString in locations) {
            parseLocation(locationString)?.let {
                LightCheckAPI.getProvider().getCachedCheckLocations().add(it)
                info("Loaded location $locationString...")
            }
        }
    }

    private fun parseLocation(input: String): Location? {
        return try {
            val parts = input.split(",", limit = 2)
            if (parts.size != 2) return null

            val worldName = parts[0]
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                err("World not found: $worldName")
                return null
            }

            val coordinates = parts[1].split(",", limit = 2)
            if (coordinates.size != 2) return null

            val position = coordinates[0].split(":")
            val rotation = coordinates[1].split(":")

            if (position.size != 3 || rotation.size != 2) return null

            val x = position[0].toDoubleOrNull() ?: return null
            val y = position[1].toDoubleOrNull() ?: return null
            val z = position[2].toDoubleOrNull() ?: return null
            val yaw = rotation[0].toFloatOrNull() ?: return null
            val pitch = rotation[1].toFloatOrNull() ?: return null

            Location(world, x, y, z, yaw, pitch)
        } catch (_: Exception) {
            err("Failed to parse location: $input")
            null
        }
    }

    companion object { 
        private lateinit var instance: Main

        internal fun getInstance(): Main {
            return instance
        }
    }

}

fun info(text: Any?) {
    if(!Main.getInstance().debug) return
    Main.getInstance().logger.info(text.toString())
}
fun err(text: Any?, t: Throwable? = null) {
    if(!Main.getInstance().debug) return
    if(t != null) Main.getInstance().logger.severe("$text\n${t.message}")
    else Main.getInstance().logger.severe(text.toString())
}
fun warn(text: Any?) {
    if(!Main.getInstance().debug) return
    Main.getInstance().logger.warning(text.toString())
}

