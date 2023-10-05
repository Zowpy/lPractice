package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BuildCommand: BaseCommand() {

    @CommandAlias("build")
    @CommandPermission("lpractice.command.build")
    fun build(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.match != null) return

        if (profile?.canBuild!!) {
            profile.canBuild = false
            player.sendMessage("${CC.RED}You can no longer build")
        } else {
            profile.canBuild = true
            player.sendMessage("${CC.GREEN}You may now build")
        }
    }
}