package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Named
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object SpectateCommand {

    @Command(name = "spectate", aliases = ["s", "spec"])
    fun spectate(@Sender player: Player, @Named("player") target: Player) {
        val targetProfile = Profile.getByUUID(target.uniqueId)
        val profile = Profile.getByUUID(player.uniqueId)

        if (targetProfile?.state != ProfileState.MATCH) {
            player.sendMessage("${CC.RED}That player in not in a match!")
            return
        }

        if (!targetProfile.settings.spectators && !player.hasPermission("lpractice.bypass.spectate")) {
            player.sendMessage("${CC.SECONDARY}${target.name}${CC.PRIMARY}'s has spectating disabled.")
            return
        }

        val match = Match.getByUUID(targetProfile.match!!)

        if (match == null) {
            player.sendMessage("${CC.RED}That player in not in a match!")
            return
        }

        if (profile?.state != ProfileState.LOBBY) return

        match.addSpectator(player)
        player.teleport(target)
    }
}