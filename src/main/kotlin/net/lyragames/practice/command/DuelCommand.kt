package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.menu.DuelSelectKitMenu
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
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

    @Command(value = ["partyduel accept"], description = "accept a player's party duel request")
    fun partyaccept(@Sender player: Player, target: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile?.party == null) {
            player.sendMessage("${CC.RED}You are not in a party!")
            return
        }

        if (profile1?.party == null) {
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

        val match = TeamMatch(duelRequest.kit!!, duelRequest.arena!!, false)
        match.friendly = true

        val team1 = match.teams[0]
        val team2 = match.teams[1]

        for (uuid in party?.players!!) {
            val profileParty = Profile.getByUUID(uuid)
            profileParty?.state = ProfileState.MATCH
            profileParty?.match = match.uuid

            match.addPlayer(Bukkit.getPlayer(uuid), team1)
        }

        for (uuid in party1?.players!!) {
            val profileParty = Profile.getByUUID(uuid)
            profileParty?.state = ProfileState.MATCH
            profileParty?.match = match.uuid

            match.addPlayer(Bukkit.getPlayer(uuid), team2)
        }

        Match.matches.add(match)
        match.start()
    }
}