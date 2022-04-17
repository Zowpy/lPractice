package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FFACommand {

    @Permission("lpractice.command.ffa.setup")
    @Command(value = ["ffa", "ffa help"])
    fun help(@Sender player: CommandSender) {
        player.sendMessage("${CC.PRIMARY}FFA Help:")
        player.sendMessage("${CC.SECONDARY}/ffa setspawn")
        player.sendMessage("${CC.SECONDARY}/ffa min")
        player.sendMessage("${CC.SECONDARY}/ffa max")
    }

    @Permission("lpractice.command.ffa.setup")
    @Command(value = ["ffa spawn", "ffa setspawn"], description = "set ffa spawn point!")
    fun spawn(@Sender player: Player) {
        Constants.FFA_SPAWN = player.location
        PracticePlugin.instance.ffaFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa spawn point!")
    }

    @Permission("lpractice.command.ffa.setup")
    @Command(value = ["ffa min"], description = "set ffa min location!")
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
    @Command(value = ["ffa max"], description = "set ffa max location!")
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