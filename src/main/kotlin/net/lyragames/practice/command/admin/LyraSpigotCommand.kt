package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player
import zone.potion.toothless.ToothlessConfig
import zone.potion.toothless.ToothlessServer

object LyraSpigotCommand {

    @Permission("lpractice.command.lyraspigot")
    @Command(name = "lyraspigot")
    fun reloadKB(@Sender player: Player) {
        ToothlessConfig.reload()

        player.sendMessage("${CC.GREEN}Successfully reloaded the knockback settings for Lyra Spigot")
    }

}