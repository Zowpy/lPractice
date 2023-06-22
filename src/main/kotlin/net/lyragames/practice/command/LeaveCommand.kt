package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player

object LeaveCommand {

    @Command(value = ["leave"], description = "leave ffa")
    fun leave(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state != ProfileState.FFA) {
            player.sendMessage("${CC.RED}You are not in FFA!")
            return
        }

        val ffa = FFAManager.getByUUID(profile.ffa!!)
        ffa!!.handleLeave(ffa.getFFAPlayer(player.uniqueId)!!, false)

        player.sendMessage("${CC.PRIMARY}You have left ${CC.SECONDARY}FFA.")
    }
}