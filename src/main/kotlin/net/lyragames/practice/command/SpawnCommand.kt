package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Subcommand

import net.lyragames.practice.Locale
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SpawnCommand: BaseCommand() {

    @CommandAlias("spawn")
    fun spawn(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.state != ProfileState.LOBBY || profile.state != ProfileState.QUEUE) {
            player.sendMessage(Locale.CANT_DO_THIS.getMessage())
            return
        }

        Hotbar.giveHotbar(profile)
        player.teleport(Constants.SPAWN)
    }
}