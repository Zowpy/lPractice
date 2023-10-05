package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Single
import co.aikar.commands.annotation.Subcommand
import net.lyragames.practice.Locale
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SpectateCommand: BaseCommand() {

    @CommandAlias("spectate|s|spec")
    fun spectate(player: CommandSender, @Single @Name("player") target: Player) {
        val targetProfile = Profile.getByUUID(target.uniqueId)
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (targetProfile?.state != ProfileState.MATCH) {
            player.sendMessage(Locale.NOT_IN_A_MATCH.getMessage())
            return
        }

        if (!targetProfile.settings.spectators && !player.hasPermission("lpractice.bypass.spectate")) {
            player.sendMessage(Locale.SPECTATING_DISABLED.getMessage())
            return
        }

        val match = Match.getByUUID(targetProfile.match!!)

        if (match == null) {
            player.sendMessage(Locale.NOT_IN_A_MATCH.getMessage())
            return
        }

        if (profile?.state != ProfileState.LOBBY) return

        match.addSpectator(player)
        player.teleport(target)
    }
}