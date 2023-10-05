package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
@CommandAlias("ffa")
@CommandPermission("lpractice.command.ffa.setup")
object FFACommand: BaseCommand() {
   @HelpCommand
   @Syntax("[page]")
   fun help(helpCommand: CommandHelp) {
        helpCommand.showHelp()

    }

    @Subcommand("spawn|setspawn")
    @Description("Set the spawn of the FFA arena")

    fun spawn( player: CommandSender) {
        Constants.FFA_SPAWN = (player as Player).location
        PracticePlugin.instance.ffaFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa spawn point!")
    }

    @Subcommand("min")
    @Description("Minimum point of the FFA safezone")
    fun min(player: CommandSender) {
        Constants.MIN = (player as Player).location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MIN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa min location!")

        if (Constants.MIN != null && Constants.MAX != null) {
            Constants.SAFE_ZONE = Cuboid(Constants.MIN!!, Constants.MAX!!)
        }
    }

    @Subcommand("max")
    @Description("Maximum point of the FFA safezone")
    fun max(player: CommandSender) {
        Constants.MAX = (player as Player).location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MAX", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()

        player.sendMessage("${CC.GREEN}Successfully set ffa max location!")

        if (Constants.MIN != null && Constants.MAX != null) {
            Constants.SAFE_ZONE = Cuboid(Constants.MIN!!, Constants.MAX!!)
        }
    }
}