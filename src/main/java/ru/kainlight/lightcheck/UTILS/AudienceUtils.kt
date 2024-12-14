package ru.kainlight.lightcheck.UTILS

import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.kainlight.lightcheck.Main

fun Player.getAudience(): Audience {
    return Main.instance.bukkitAudiences.player(this)
}
fun CommandSender.getAudience(): Audience {
    return Main.instance.bukkitAudiences.sender(this)
}

object AudienceUtils {

    fun Main.getAudience(player: Player): Audience {
        return this.bukkitAudiences.player(player)
    }
    fun Main.getAudience(sender: CommandSender): Audience {
        return this.bukkitAudiences.sender(sender)
    }

}