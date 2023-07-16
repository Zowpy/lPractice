package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Named
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.party.Party
import net.lyragames.practice.party.PartyType
import net.lyragames.practice.party.invitation.PartyInvitation
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.TextBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PartyCommand {

    @Command(name = "party", aliases = ["p"])
    fun help(@Sender player: Player) {
        player.sendMessage("${CC.PRIMARY}Party Commands:")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/party create")
        player.sendMessage("${CC.SECONDARY}/party disband")
        player.sendMessage("${CC.SECONDARY}/party leave")
        player.sendMessage("${CC.SECONDARY}/party join <player>")
        player.sendMessage("${CC.SECONDARY}/party invite <player>")
        player.sendMessage(CC.translate("&7&m---------------------"))
    }

    @Command(name = "party create", aliases = ["p create"])
    fun create(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party != null) {
            player.sendMessage("${CC.RED}You are already in a party!")
            return
        }

        val party = Party(player.uniqueId)
        party.players.add(player.uniqueId)

        PartyManager.parties.add(party)

        profile?.party = party.uuid

        Hotbar.giveHotbar(profile!!)
        player.sendMessage("${CC.GREEN}Successfully created party!")
    }

    @Command(name = "party disband", aliases = ["p disband"])
    fun disband(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party == null) {
            player.sendMessage("${CC.RED}You are not in a party.")
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)

        party?.players?.map { Profile.getByUUID(it) }
            ?.forEach {
                it?.party = null
                it?.player?.sendMessage("${CC.RED}The party has been disbanded.")
                Hotbar.giveHotbar(it!!)
            }

        PartyManager.parties.remove(party)
    }

    @Command(name = "party leave", aliases = ["p leave"])
    fun leave(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party == null) {
            player.sendMessage("${CC.RED}You are not in a party.")
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)

        if (party?.leader == player.uniqueId) {
            party?.players?.filter { Bukkit.getPlayer(it) != null }?.map { Profile.getByUUID(it) }
                ?.forEach {
                    it?.party = null
                    it?.player?.sendMessage("${CC.RED}The party has been disbanded.")
                    Hotbar.giveHotbar(it!!)
                }

            PartyManager.parties.remove(party)
        } else {
            party?.players?.remove(player.uniqueId)
            profile.party = null
            Hotbar.giveHotbar(profile)
            party?.sendMessage("${CC.SECONDARY}${player.name}${CC.PRIMARY} left the party!")
        }
    }

    @Command(name = "party invite", aliases = ["p invite"])
    fun invite(@Sender player: Player, @Named("player") target: Player) {
        if (player.uniqueId.equals(target.uniqueId)) {
            player.sendMessage("${CC.RED}You can't invite yourself!")
            return
        }

        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party == null) {
            player.sendMessage("${CC.RED}You are not in a party!")
            return
        }

        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile1?.party != null) {
            player.sendMessage("${CC.RED}That player is already in a party!")
            return
        }

        if (profile1?.getPartyInvite(profile.party!!) != null) {
            player.sendMessage("${CC.RED}You already invited that player!")
            return
        }

        val partyInvite = PartyInvitation(profile.party!!, target.uniqueId)
        profile1?.partyInvites?.add(partyInvite)

        val message = TextBuilder()
            .setText("${CC.PRIMARY}You have been invited to ${CC.SECONDARY}${player.name}'s ${CC.PRIMARY}party!")
            .then()
            .setText(" ${CC.SECONDARY}[Click to join]")
            .setCommand("/party join ${player.name}") //${profile.party?.toString()}")
            .build()

        target.spigot().sendMessage(message)

        player.sendMessage("${CC.PRIMARY}Successfully invited ${CC.SECONDARY}${target.name}${CC.PRIMARY}!")
    }

    @Command(name = "party join", aliases = ["p join"])
    fun join(@Sender player: Player, @Named("player") target: Player) {

        if (player.uniqueId == target.uniqueId) {
            player.sendMessage("${CC.RED}You can't join your own party!")
            return
        }

        val profile = Profile.getByUUID(player.uniqueId)
        val profile1 = Profile.getByUUID(target.uniqueId)

        if (profile?.party != null) {
            player.sendMessage("${CC.RED}You are already in a party!")
            return
        }

        if (profile1?.party == null) {
            player.sendMessage("${CC.RED}That player isn't in a party")
            return
        }

        val party = PartyManager.getByUUID(profile1.party!!)

        if (party?.leader != target.uniqueId) {
            player.sendMessage("${CC.RED}That player isn't in a party")
            return
        }

        val partyInvitation = profile?.getPartyInvite(profile1.party!!)

        if (party?.banned?.contains(player.uniqueId)!!) {
            player.sendMessage("${CC.RED}You are banned from this party!")
            return
        }

        if (party.partyType == PartyType.PRIVATE && partyInvitation == null) {
            player.sendMessage("${CC.RED}You are not invited to this party!")
            return
        }

        if (partyInvitation != null && partyInvitation.isExpired() && party.partyType == PartyType.PRIVATE) {
            player.sendMessage("${CC.RED}That party invite expired!")
            return
        }

        party.players.add(player.uniqueId)
        profile?.party = party.uuid

        if (partyInvitation != null) {
            profile.partyInvites.remove(partyInvitation)
        }

        Hotbar.giveHotbar(profile!!)

        party.sendMessage("${CC.SECONDARY}${player.name}${CC.PRIMARY} joined the party!")
    }
}