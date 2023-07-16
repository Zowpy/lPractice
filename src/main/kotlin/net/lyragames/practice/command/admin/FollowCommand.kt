package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/26/2022
 * Project: lPractice
 */

object FollowCommand {

    @Command(name = "follow")
    fun follow(@Sender player: Player, target: Player) {
        if (true) {
            player.sendMessage("${CC.RED}This command is currently disabled!")
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)
        val profile1 = Profile.getByUUID(player.uniqueId)

        if (profile1?.state != ProfileState.LOBBY) return

        if (profile!!.followers.contains(player.uniqueId)) {
            player.sendMessage("${CC.RED}You are already following ${target.name}.")
            return
        }

        profile.followers.add(player.uniqueId)
        profile1.following = true

        player.sendMessage("${CC.PRIMARY}Started following ${CC.SECONDARY}${target.name}")
    }

}