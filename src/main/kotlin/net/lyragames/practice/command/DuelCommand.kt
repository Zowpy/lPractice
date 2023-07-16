package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Named
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.menu.DuelSelectKitMenu
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object DuelCommand {

    @Command(name = "duel")
    fun duel(@Sender player: Player, @Named("player") target: Player) {
        if (player.uniqueId == target.uniqueId) {
            player.sendMessage("${CC.RED}You can't duel yourself.")
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)

        if (profile!!.duelRequests.any { it.uuid == player.uniqueId && !it.isExpired() }) {
            player.sendMessage("${CC.RED}You already have an ongoing duel request to ${target.name}.")
            return
        }

        if (profile.state != ProfileState.LOBBY) {
            player.sendMessage("${CC.RED}That player is currently busy!")
            return
        }

        if (!profile.settings.duels && !player.hasPermission("lpractice.bypass.duels")) {
            player.sendMessage("${CC.SECONDARY}${target.name}${CC.PRIMARY}'s has duels disabled.")
            return
        }

        val duelProcedure = DuelProcedure(player.uniqueId, target.uniqueId)
        DuelProcedure.duelProcedures.add(duelProcedure)

        DuelSelectKitMenu().openMenu(player)
    }

    @Command(name = "duel accept")
    fun accept(@Sender player: Player, @Named("player") target: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        val duelRequest = profile?.getDuelRequest(target.uniqueId)

        if (duelRequest == null) {
            player.sendMessage("${CC.RED}Invalid duel request!")
            return
        }

        val arena = duelRequest.arena
        profile.duelRequests.remove(duelRequest)

        MatchManager.createMatch(
            duelRequest.kit,
            arena,
            false,
            true,
            player,
            target
        )
    }

    @Command(name = "partyduel accept")
    fun partyaccept(@Sender player: Player, @Named("player") target: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile?.state != ProfileState.LOBBY || profile1?.state != ProfileState.LOBBY) {
            player.sendMessage("${CC.RED}You can't do this right now!")
            return
        }

        if (profile.party == null) {
            player.sendMessage("${CC.RED}You are not in a party!")
            return
        }

        if (profile1.party == null) {
            player.sendMessage("${CC.RED}That player is not in a party!")
            return
        }

        if (profile.party == profile1.party) {
            player.sendMessage("${CC.RED}You are in ${CC.YELLOW}${target.name}'s ${CC.RED}party!")
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)
        val party1 = PartyManager.getByUUID(profile1.party!!)

        if (party?.leader != player.uniqueId) {
            player.sendMessage("${CC.RED}Only the party leader can accept duel requests!")
            return
        }

        val duelRequest = party?.getDuelRequest(profile1.uuid)

        if (duelRequest == null) {
            player.sendMessage("${CC.RED}Invalid duel request!")
            return
        }

        MatchManager.createTeamMatch(
            duelRequest.kit!!,
            duelRequest.arena!!,
            false,
            true,
            party.players,
            party1!!.players
        )
    }
}