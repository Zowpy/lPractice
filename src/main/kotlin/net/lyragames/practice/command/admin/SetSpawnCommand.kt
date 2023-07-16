package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.entity.Player

object SetSpawnCommand {

    @Command(name = "setspawn")
    @Permission("lpractice.command.set.spawn")
    fun setspawn(@Sender player: Player) {
        Constants.SPAWN = player.location
        PracticePlugin.instance.settingsFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.settingsFile.save()

        player.sendMessage("${CC.GREEN}Successfully set spawn!")
    }
}