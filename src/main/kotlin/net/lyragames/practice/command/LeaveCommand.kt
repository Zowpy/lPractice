package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.entity.Player

object LeaveCommand {

    @Command(value = ["leave"], description = "leave ffa")
    fun leave(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state != ProfileState.FFA) {
            player.sendMessage("${CC.RED}You are not in FFA!")
            return
        }

        profile.state = ProfileState.LOBBY
        val ffa = FFAManager.getByUUID(profile.ffa!!)

        ffa?.players?.removeIf { it.uuid == player.uniqueId }

        PlayerUtil.reset(player)

        if (Constants.SPAWN != null) {
            player.teleport(Constants.SPAWN)
        }

        ffa?.players?.stream()?.map { it.player }
            ?.forEach {
                player.hidePlayer(it)
                it.hidePlayer(player)
            }
             Hotbar.giveHotbar(profile)
             if (Constants.SPAWN != null) {
                 player.teleport(Constants.SPAWN)
             }
    }
}