package net.lyragames.practice.party.duel

import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.PartyManager
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/3/2022
 * Project: lPractice
 */

class PartyDuelRequest(val partyUUID: UUID, val issuer: UUID) {

    private val executedAt = System.currentTimeMillis()
    var kit: Kit? = null
    var arena: Arena? = null

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60_000
    }

    fun send() {

        val party = PartyManager.getByUUID(partyUUID)
        val sender = Bukkit.getPlayer(issuer)
        val leader = Bukkit.getPlayer(party?.leader)

        party?.duelRequests?.add(this)

        FancyMessage()
            .text("${CC.PINK}${sender.name}${CC.GREEN}${CC.YELLOW}'s party has sent your party a duel request with kit ${CC.PINK}${kit?.name}${CC.YELLOW} on")
            .then()
            .text("${CC.YELLOW} arena ${CC.PINK}${arena?.name}${CC.YELLOW}.")
            .then()
            .text("${CC.GREEN} [Click to accept]")
            .command("/partyduel accept ${sender.name}")
            .send(leader)
    }
}