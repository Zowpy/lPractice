package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.Locale
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object LeaveCommand {

    @Command(name = "leave", description = "leave ffa")
    fun leave(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state != ProfileState.FFA) {
            player.sendMessage(Locale.NOT_IN_FFA.getMessage())
            return
        }

        val ffa = FFAManager.getByUUID(profile.ffa!!)
        ffa!!.handleLeave(ffa.getFFAPlayer(player.uniqueId)!!, false)

        player.sendMessage(Locale.LEFT_FFA.getMessage())
    }
}