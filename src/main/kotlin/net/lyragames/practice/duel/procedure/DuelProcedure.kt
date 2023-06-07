package net.lyragames.practice.duel.procedure

import net.lyragames.practice.arena.Arena
import net.lyragames.practice.duel.DuelRequest
import net.lyragames.practice.kit.Kit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/28/2022
 * Project: lPractice
 */

class DuelProcedure(var uuid: UUID, var target: UUID) {

    var stage = DuelProcedureStage.KIT
    var kit: Kit? = null
    var arena: Arena? = null

    fun create(): DuelRequest {
        return DuelRequest(uuid, target, kit!!, arena!!)
    }

    companion object {
        @JvmStatic
        val duelProcedures: MutableList<DuelProcedure> = mutableListOf()

        fun getByUUID(uuid: UUID): DuelProcedure? {
            return duelProcedures.stream().filter { it.uuid == uuid }
                .findAny().orElse(null)
        }
    }
}