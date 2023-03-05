package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import org.bukkit.entity.Player

object SetSpawnCommand {

    @Command(value = ["setspawn"])
    @Permission("lpractice.command.set.spawn")
    fun setspawn(@Sender player: Player) {
        Constants.SPAWN = player.location
        PracticePlugin.instance.settingsFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.settingsFile.save()

        player.sendMessage("${CC.GREEN}Successfully set spawn!")
    }
}