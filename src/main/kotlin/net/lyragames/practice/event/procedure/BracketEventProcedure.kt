package net.lyragames.practice.event.procedure

import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.kit.Kit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/21/2022
 * Project: lPractice
 */

class BracketEventProcedure(var uuid: UUID, var eventMap: EventMap) {

    var kit: Kit? = null

    companion object {

        @JvmStatic
        val procedures: MutableMap<UUID, BracketEventProcedure> = mutableMapOf()
    }
}