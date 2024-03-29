package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import net.lyragames.practice.Locale
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.party.Party
import net.lyragames.practice.party.PartyType
import net.lyragames.practice.party.invitation.PartyInvitation
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.TextBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("party|p")
object PartyCommand: BaseCommand() {

    @HelpCommand
    @Syntax("[page]")
    fun help(help: CommandHelp) {
        help.showHelp()
        /*
        player.sendMessage("${CC.PRIMARY}Party Commands:")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/party create")
        player.sendMessage("${CC.SECONDARY}/party disband")
        player.sendMessage("${CC.SECONDARY}/party leave")
        player.sendMessage("${CC.SECONDARY}/party join <player>")
        player.sendMessage("${CC.SECONDARY}/party invite <player>")
        player.sendMessage(CC.translate("&7&m---------------------"))

         */
    }

    @Subcommand("create")
    fun create(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.party != null) {
            player.sendMessage(Locale.ALREADY_IN_PARTY.getMessage())
            return
        }

        val party = Party(player.uniqueId)
        party.players.add(player.uniqueId)

        PartyManager.parties.add(party)

        profile?.party = party.uuid

        Hotbar.giveHotbar(profile!!)
        player.sendMessage(Locale.CREATED_PARTY.getMessage())
    }

    @Subcommand("disband")
    fun disband(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.party == null) {
            player.sendMessage(Locale.NOT_IN_A_PARTY.getMessage())
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)

        party?.players?.map { Profile.getByUUID(it) }
            ?.forEach {
                it?.party = null
                it?.player?.sendMessage(Locale.DISBANDED_PARTY.getMessage())
                Hotbar.giveHotbar(it!!)
            }

        PartyManager.parties.remove(party)
    }

    @Subcommand("leave")
    fun leave(player: CommandSender) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (profile?.party == null) {
            player.sendMessage(Locale.NOT_IN_A_PARTY.getMessage())
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)

        if (party?.leader == player.uniqueId) {
            party?.players?.filter { Bukkit.getPlayer(it) != null }?.map { Profile.getByUUID(it) }
                ?.forEach {
                    it?.party = null
                    it?.player?.sendMessage(Locale.DISBANDED_PARTY.getMessage())
                    Hotbar.giveHotbar(it!!)
                }

            PartyManager.parties.remove(party)
        } else {
            party?.players?.remove(player.uniqueId)
            profile.party = null
            Hotbar.giveHotbar(profile)
            party?.sendMessage(Locale.LEFT_PARTY.getMessage())
        }
    }

    @Subcommand("invite")
    fun invite(player: CommandSender, @Single @Name("target") target: Player) {
        if ((player as Player).uniqueId.equals(target.uniqueId)) {
            player.sendMessage(Locale.CANT_INVITE_YOURSELF.getMessage())
            return
        }

        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party == null) {
            player.sendMessage(Locale.NOT_IN_A_PARTY.getMessage())
            return
        }

        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile1?.party != null) {
            player.sendMessage(Locale.PLAYER_ALREADY_IN_PARTY.getMessage())
            return
        }

        if (profile1?.getPartyInvite(profile.party!!) != null) {
            player.sendMessage(Locale.ALREADY_INVITED_PLAYER.getMessage())
            return
        }

        val partyInvite = PartyInvitation(profile.party!!, target.uniqueId)
        profile1?.partyInvites?.add(partyInvite)

        val message = TextBuilder()
            .setText(Locale.PARTY_INVITED_MESSAGE.getMessage())
            .then()
            .setText(Locale.CLICK_TO_JOIN.getMessage())
            .setCommand("/party join ${player.name}") //${profile.party?.toString()}")
            .then()
            .build()

        target.spigot().sendMessage(message)

        player.sendMessage(Locale.PARTY_INVITED_MESSAGE.getMessage())
    }

    @Subcommand("join")
    fun join(player: CommandSender,@Single @Name("player") target: Player) {

        if ((player as Player).uniqueId == target.uniqueId) {
            player.sendMessage(Locale.JOIN_OWN_PARTY.getMessage())
            return
        }

        val profile = Profile.getByUUID(player.uniqueId)
        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile?.party != null) {
            player.sendMessage(Locale.ALREADY_IN_PARTY.getMessage())
            return
        }

        if (profile1?.party == null) {
            player.sendMessage(Locale.ISNT_IN_PARTY.getMessage())
            return
        }

        val party = PartyManager.getByUUID(profile1.party!!)

        if (party?.leader != target.uniqueId) {
            player.sendMessage(Locale.ISNT_IN_PARTY.getMessage())
            return
        }

        val partyInvitation = profile?.getPartyInvite(profile1.party!!)

        if (party?.banned?.contains(player.uniqueId)!!) {
            player.sendMessage(Locale.BANNED_FROM_PARTY.getMessage())
            return
        }

        if (party.partyType == PartyType.PRIVATE && partyInvitation == null) {
            player.sendMessage(Locale.NOT_INVITED.getMessage())
            return
        }

        if (partyInvitation != null && partyInvitation.isExpired() && party.partyType == PartyType.PRIVATE) {
            player.sendMessage(Locale.PARTY_EXPIRED.getMessage())
            return
        }

        party.players.add(player.uniqueId)
        profile?.party = party.uuid

        if (partyInvitation != null) {
            profile.partyInvites.remove(partyInvitation)
        }

        Hotbar.giveHotbar(profile!!)

        party.sendMessage(Locale.JOIN_PARTY_BROADCAST.getMessage())
    }

    @Subcommand("accept")
    @Async
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