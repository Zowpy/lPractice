package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player

object SpectateCommand {

    @Command(value = ["spectate", "s"], description = "spectate a player's match")
    fun spectate(@Sender player: Player, target: Player) {
        val profile = Profile.getByUUID(target.uniqueId)

        if (profile?.state != ProfileState.MATCH) {
            player.sendMessage("${CC.RED}That player in not in a match!")
            return
        }

        if (!profile.settings.spectators && !player.hasPermission("lpractice.bypass.spectate")) {
            player.sendMessage("${CC.SECONDARY}${target.name}${CC.PRIMARY}'s spectating is off.")
            return
        }

        val match = Match.getByUUID(profile.match!!)

        if (match == null) {
            player.sendMessage("${CC.RED}That player in not in a match!")
            return
        }

        match.addSpectator(player)
        player.teleport(target)
    }
}