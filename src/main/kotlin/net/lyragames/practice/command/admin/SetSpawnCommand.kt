package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SetSpawnCommand: BaseCommand() {

    @CommandAlias("setspawn")
    @CommandPermission("lpractice.admin")
    fun setspawn(player: CommandSender) {
        Constants.SPAWN = (player as Player).location
        PracticePlugin.instance.settingsFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.settingsFile.save()

        player.sendMessage("${CC.GREEN}Successfully set spawn!")
    }
}