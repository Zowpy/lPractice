package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.menu.DuelSelectKitMenu
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player

object DuelCommand {

    @Command(value = ["duel"], description = "send a duel request to a player!")
    fun duel(@Sender player: Player, target: Player) {
        if (player.uniqueId == target.uniqueId) {
            player.sendMessage("${CC.RED}You can't duel yourself.")
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)

        if (profile?.state != ProfileState.LOBBY) {
            player.sendMessage("${CC.RED}That player is currently busy!")
            return
        }

        val duelProcedure = DuelProcedure(player.uniqueId, target.uniqueId)
        DuelProcedure.duelProcedures.add(duelProcedure)

        DuelSelectKitMenu().openMenu(player)
    }

    @Command(value = ["duel accept"], description = "accept a player's duel request")
    fun accept(@Sender player: Player, target: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        val duelRequest = profile?.getDuelRequest(target.uniqueId)

        if (duelRequest == null) {
            player.sendMessage("${CC.RED}Invalid duel request!")
            return
        }

        val match = Match(duelRequest.kit, duelRequest.arena, false)
        match.addPlayer(player, duelRequest.arena.l1!!)
        match.addPlayer(target, duelRequest.arena.l2!!)

        profile.match = match.uuid
        profile.state = ProfileState.MATCH

        val profile1 = Profile.getByUUID(target.uniqueId)
        profile1?.state = ProfileState.MATCH
        profile1?.match = match.uuid

        Match.matches.add(match)
        match.start()
    }
}