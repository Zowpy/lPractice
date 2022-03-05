package net.lyragames.practice.party.duel.procedure

import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.party.duel.PartyDuelRequest
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/3/2022
 * Project: lPractice
 */

class PartyDuelProcedure(val issuer: UUID) {

    var party: UUID? = null
    var kit: Kit? = null
    var arena: Arena? = null

    fun create(): PartyDuelRequest {
        val partyDuelRequest = PartyDuelRequest(party!!, issuer)
        partyDuelRequest.kit = kit
        partyDuelRequest.arena = arena

        return partyDuelRequest
    }

    companion object {

        @JvmStatic
        val duelProcedures: MutableList<PartyDuelProcedure> = mutableListOf()

        fun getByUUID(uuid: UUID): PartyDuelProcedure? {
            return duelProcedures.stream().filter { it.issuer == uuid }
                .findFirst().orElse(null)
        }
    }
}