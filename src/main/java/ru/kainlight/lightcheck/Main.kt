package ru.kainlight.lightcheck

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.HandlerList
import ru.kainlight.lightcheck.COMMANDS.Check
import ru.kainlight.lightcheck.COMMANDS.Completer
import ru.kainlight.lightcheck.EVENTS.CheckedListener
import ru.kainlight.lightcheck.UTILS.Runnables
import ru.kainlight.lightlibrary.LightConfig
import ru.kainlight.lightlibrary.LightPlugin
import ru.kainlight.lightlibrary.UTILS.Init
import ru.kainlight.lightlibrary.UTILS.Parser

class Main : LightPlugin() {

    lateinit var bukkitAudiences: BukkitAudiences
    lateinit var runnables: Runnables

    override fun onLoad() {
        this.saveDefaultConfig()

        configurationVersion = 1.4
        updateConfig()
        LightConfig.saveLanguages(this, "language")
        messageConfig.configurationVersion = 1.4
        messageConfig.updateConfig()
    }

    override fun onEnable() {
        instance = this

        this.reloadParseMode()

        this.bukkitAudiences = BukkitAudiences.create(this)

        this.runnables = Runnables(this)

        this.registerCommand("lightcheck", Check(this), Completer(this))
        this.registerListener(CheckedListener(this))

        Init.start(this, true)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        this.server.scheduler.cancelTasks(this)

        Init.stop(this)
    }

    fun getMessageConfig(): FileConfiguration {
        return this.messageConfig.getConfig()
    }

    fun reloadParseMode() {
        Parser.parseMode = this.config.getString("settings.parse_mode", "MINIMESSAGE")!!
    }

    companion object {
        @JvmStatic lateinit var instance: Main
    }

}

