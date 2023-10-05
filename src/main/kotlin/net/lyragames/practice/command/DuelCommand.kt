package net.lyragames.practice.command


import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import net.lyragames.practice.Locale
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.menu.DuelSelectKitMenu
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("duel")
object DuelCommand: BaseCommand() {

    @Default
    @Async
    fun duel(sender: CommandSender, @Single @Name("other") target: Player) {
        val player = sender as Player
        if (player.uniqueId == target.uniqueId) {
            player.sendMessage(Locale.CANT_DUEL_YOURSELF.getMessage())
            return
        }

        val profile = Profile.getByUUID(target.uniqueId)

        if (profile!!.duelRequests.any { it.uuid == player.uniqueId && !it.isExpired() }) {
            player.sendMessage(Locale.ONGOING_DUEL.getMessage().replace("<target>", target.name))
            return
        }

        if (profile.state != ProfileState.LOBBY) {
            player.sendMessage(Locale.BUSY_PLAYER.getMessage())
            return
        }

        if (!profile.settings.duels && !player.hasPermission("lpractice.bypass.duels")) {
            player.sendMessage(Locale.DISABLED_DUELS.getMessage())
            return
        }

        val duelProcedure = DuelProcedure(player.uniqueId, target.uniqueId)
        DuelProcedure.duelProcedures.add(duelProcedure)

        DuelSelectKitMenu().openMenu(player)
    }

    @Subcommand("accept")
    @Async
    fun accept(player: CommandSender, @Single @Name("player") target: Player) {
        val profile = Profile.getByUUID((player as Player).uniqueId)
        val duelRequest = profile?.getDuelRequest(target.uniqueId)

        if (duelRequest == null) {
            player.sendMessage(Locale.INVALID_DUEL.getMessage())
            return
        }

        val arena = duelRequest.arena
        profile.duelRequests.remove(duelRequest)

        MatchManager.createMatch(
            duelRequest.kit,
            arena,
            false,
            true,
            player as Player,
            target
        )
    }

    @CommandAlias("party accept")
    fun partyaccept( player: CommandSender,@Single @Name("player") target: Player) {
        val profile = Profile.getByUUID((player as Player).uniqueId)
        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile?.state != ProfileState.LOBBY || profile1?.state != ProfileState.LOBBY) {
            player.sendMessage(Locale.CANT_DO_THIS.getMessage())
            return
        }

        if (profile.party == null) {
            player.sendMessage(Locale.NOT_IN_A_PARTY.getNormalMessage())
            return
        }

        if (profile1.party == null) {
            player.sendMessage(Locale.OTHER_NOT_IN_A_PARTY.getMessage())
            return
        }

        if (profile.party == profile1.party) {
            player.sendMessage(Locale.JOINED_PARTY.getMessage())
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)
        val party1 = PartyManager.getByUUID(profile1.party!!)

        if (party?.leader != player.uniqueId) {
            player.sendMessage(Locale.CANT_ACCEPT_PARTY_DUEL.getMessage())
            return
        }

        val duelRequest = party?.getDuelRequest(profile1.uuid)

        if (duelRequest == null) {
            player.sendMessage(Locale.INVALID_DUEL.getMessage())
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