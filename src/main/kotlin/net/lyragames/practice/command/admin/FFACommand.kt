package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FFACommand {

    @Permission("lpractice.command.ffa.setup")
    @Command(name = "ffa", aliases = ["ffa help"])
    fun help(@Sender player: CommandSender) {
        player.sendMessage("${CC.PRIMARY}FFA Help:")
        player.sendMessage("${CC.SECONDARY}/ffa setspawn")
        player.sendMessage("${CC.SECONDARY}/ffa min")
        player.sendMessage("${CC.SECONDARY}/ffa max")
    }

    @Permission("lpractice.command.ffa.setup")
    @Command(name = "ffa spawn", aliases = ["ffa setspawn"])
    fun spawn(@Sender player: Player) {
        Constants.FFA_SPAWN = player.location
        PracticePlugin.instance.ffaFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa spawn point!")
    }

    @Permission("lpractice.command.ffa.setup")
    @Command(name = "ffa min")
    fun min(@Sender player: Player) {
        Constants.MIN = player.location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MIN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa min location!")

        if (Constants.MIN != null && Constants.MAX != null) {
            Constants.SAFE_ZONE = Cuboid(Constants.MIN, Constants.MAX)
        }
    }

    @Permission("lpractice.command.ffa.setup")
    @Command(name = "ffa max")
    fun max(@Sender player: Player) {
        Constants.MAX = player.location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MAX", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa max location!")

        if (Constants.MIN != null && Constants.MAX != null) {
            Constants.SAFE_ZONE = Cuboid(Constants.MIN, Constants.MAX)
        }
    }
}