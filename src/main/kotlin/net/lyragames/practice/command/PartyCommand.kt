package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.party.Party
import net.lyragames.practice.party.PartyType
import net.lyragames.practice.party.invitation.PartyInvitation
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PartyCommand {

    @Command(value = ["party create", "p create"], description = "create a party")
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

    @Command(value = ["party disband", "p disband"], description = "close a party")
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

    @Command(value = ["party leave", "p leave"], description = "leave a party")
    fun leave(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.party == null) {
            player.sendMessage("${CC.RED}You are not in a party.")
            return
        }

        val party = PartyManager.getByUUID(profile.party!!)

        if (party?.leader == player.uniqueId) {
            party?.players?.map { Profile.getByUUID(it) }
                ?.forEach {
                    it?.party = null
                    it?.player?.sendMessage("${CC.RED}The party has been disbanded.")
                    Hotbar.giveHotbar(it!!)
                }

            PartyManager.parties.remove(party)
        }else {
            party?.players?.remove(player.uniqueId)
            profile.party = null
            Hotbar.giveHotbar(profile)
            party?.sendMessage("${CC.YELLOW}${player.name}${CC.GREEN} left the party!")
        }
    }

    @Command(value = ["party invite", "p invite"], description = "invite a player to your party")
    fun invite(@Sender player: Player, target: Player) {
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

        FancyMessage()
            .color(ChatColor.YELLOW)
            .text("You have been invited to ${CC.GREEN}${player.name}'s ${CC.YELLOW}party!")
            .then()
            .color(ChatColor.GREEN)
            .text(" [Click to join]")
            .command("/party join ${player.name}") //${profile.party?.toString()}")
            .tooltip("Click to join the party!")
            .send(target)

        player.sendMessage("${CC.GREEN}Successfully invited ${CC.YELLOW}${target.name}${CC.GREEN}!")
    }

    @Command(value = ["party join", "p join"], description = "join a person's party")
    fun join(@Sender player: Player, target: Player) {

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

        party.sendMessage("${CC.YELLOW}${player.name}${CC.GREEN} joined the party!")
    }
}