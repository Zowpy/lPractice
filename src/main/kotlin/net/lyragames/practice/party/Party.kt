package net.lyragames.practice.party

import net.lyragames.practice.party.duel.PartyDuelRequest
import net.lyragames.practice.utils.CC
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/22/2022
 * Project: lPractice
 */

class Party(var leader: UUID) {
    
    val uuid: UUID = UUID.randomUUID()

    val players: MutableList<UUID> = mutableListOf()
    val duelRequests: MutableList<PartyDuelRequest> = mutableListOf()
    val banned: MutableList<UUID> = mutableListOf()
    var partyType = PartyType.PRIVATE

    fun sendMessage(string: String) {
        players.stream().forEach {
            val player = Bukkit.getPlayer(it) ?: return@forEach

            player.sendMessage(CC.translate(string))
        }
    }

    fun getDuelRequest(uuid: UUID): PartyDuelRequest? {
        return duelRequests.stream().filter { it.issuer == uuid }
            .findFirst().orElse(null)
    }
}