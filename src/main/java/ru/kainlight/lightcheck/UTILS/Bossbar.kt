package ru.kainlight.lightcheck.UTILS

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.scheduler.BukkitScheduler
import ru.kainlight.lightcheck.API.CheckedPlayer
import ru.kainlight.lightcheck.API.LightCheckAPI
import ru.kainlight.lightcheck.Main
import ru.kainlight.lightlibrary.UTILS.Parser

class Bossbar(val plugin: Main,
    val checkedPlayer: CheckedPlayer,
    val bossBar: BossBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID),
    val timer: Long = checkedPlayer.timer,
    val enabled: Boolean = plugin.config.getBoolean("settings.bossbar")
){

    init {
        bossBar.progress = 1.0
    }

    fun show(): Boolean {
        if (!enabled) return false
        if(checkedPlayer == null) return false
        val playerTimer: Long? = checkedPlayer.timer

        if (playerTimer == null || playerTimer <= 0) {
            this.hide()
            return false
        }

        if (! LightCheckAPI.get().isChecking(checkedPlayer)) {
            this.hide()
            return false
        }

        val bossbarText: String? = plugin.getMessageConfig().getString("screen.bossbar")
        if (bossbarText == null || bossbarText.isEmpty()) return false
        val newName: String = bossbarText.replace("#seconds#", playerTimer.toString())
        bossBar.setTitle(Parser.hexString(newName))
        bossBar.progress = (playerTimer / timer).toDouble()

        bossBar.addPlayer(checkedPlayer.player)
        return true;
    }

    fun hide() {
        val scheduler: BukkitScheduler = plugin.server.scheduler

        val bossbarText: String? = plugin.getMessageConfig().getString("screen.disprove-title")
        if (bossbarText == null || bossbarText.isEmpty()) {
            this.bossBar.removePlayer(checkedPlayer.player)
            return
        }

        bossBar.setTitle(Parser.hexString(bossbarText))
        bossBar.progress = 0.0

        scheduler.runTaskLater(plugin, Runnable {
            this.bossBar.removePlayer(checkedPlayer.player)

            val sch: MutableMap<CheckedPlayer, Int> = plugin.runnables.bossbarScheduler
            val remove: Int? = sch.get(checkedPlayer)
            if(remove == null) return@Runnable
            scheduler.cancelTask(remove)
            sch.remove(checkedPlayer)
        }, 20L * 2L)
    }
}