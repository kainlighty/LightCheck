package ru.kainlight.lightcheck.UTILS

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.UTILS.Parser

internal class Bossbar(private val plugin: Main,
                       private val checkedPlayer: CheckedPlayer,
                       private val bossBar: BossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID),
                       private val timer: Long = checkedPlayer.timer,
                       private val enabled: Boolean = plugin.config.getBoolean("settings.bossbar")
){

    init {
        bossBar.progress = 1.0
    }

    fun show(): Boolean {
        if (!enabled) return false
        if(!checkedPlayer.player.isOnline) return false
        val playerTimer: Long? = checkedPlayer.timer

        if (playerTimer == null || playerTimer <= 0) {
            this.hide()
            return false
        }

        if (! LightCheckAPI.getProvider().isChecking(checkedPlayer.player)) {
            this.hide()
            return false
        }

        val bossbarText: String? = plugin.getMessagesConfig().getString("screen.bossbar")
        if (bossbarText.isNullOrEmpty()) return false

        val newName: String = bossbarText.replace("#seconds#", playerTimer.toString())
        bossBar.setTitle(Parser.hexString(newName))
        bossBar.progress = (playerTimer / timer).toDouble()

        bossBar.addPlayer(checkedPlayer.player)
        bossBar.addPlayer(checkedPlayer.inspector.player)
        return true
    }

    fun hide() {
        val bossbarText: String? = plugin.getMessagesConfig().getString("screen.disprove-title")
        if (bossbarText == null || bossbarText.isEmpty()) {
            this.bossBar.removePlayer(checkedPlayer.player)
            return
        }

        bossBar.setTitle(Parser.hexString(bossbarText))
        bossBar.progress = 0.0

        plugin.runTaskLater(Runnable {
            val player = checkedPlayer.player
            val inspectorPlayer = checkedPlayer.inspector.player

            this.bossBar.removePlayer(player)
            this.bossBar.removePlayer(inspectorPlayer)

            val sch: MutableMap<Player, Int> = plugin.runnables.bossbarScheduler
            val remove: Int? = sch.get(player)
            if(remove == null) return@Runnable

            plugin.cancelTask(remove)
            sch.remove(player)
            sch.remove(inspectorPlayer)
        }, 20L * 2L)
    }
}