package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Name

import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import co.aikar.commands.annotation.Single as Single


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/26/2022
 * Project: lPractice
 */

object FollowCommand: BaseCommand() {

    @CommandAlias("follow")
    fun follow(player: CommandSender, @Single @Name("target") target: Player) {
        if (true) {
            player.sendMessage("${CC.RED}This command is currently disabled!")
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)
        val profile1 = Profile.getByUUID((player as Player).uniqueId)

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