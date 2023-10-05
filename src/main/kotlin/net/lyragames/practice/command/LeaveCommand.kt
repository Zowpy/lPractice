package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias

import net.lyragames.practice.Locale
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object LeaveCommand: BaseCommand() {

    @CommandAlias("leave")
    fun leave(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.state != ProfileState.FFA) {
            player.sendMessage(Locale.NOT_IN_FFA.getMessage())
            return
        }

        val ffa = FFAManager.getByUUID(profile.ffa!!)
        ffa!!.handleLeave(ffa.getFFAPlayer((player as Player).uniqueId)!!, false)

        player.sendMessage(Locale.LEFT_FFA.getMessage())
    }
}