package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 3/26/2022
 * Project: lPractice
 */

object FollowCommand {

    @Command(value = ["follow"], description = "follow a player")
    fun follow(@Sender player: Player, target: Player) {
        val profile = Profile.getByUUID(target.uniqueId)
        val profile1 = Profile.getByUUID(player.uniqueId)

        if (profile1?.state != ProfileState.LOBBY) return
        profile?.followers?.add(player.uniqueId)
        profile1?.following = true

        player.sendMessage("${CC.PRIMARY}Started following ${CC.SECONDARY}${target.name}")
    }
}