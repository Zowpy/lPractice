package net.lyragames.practice.manager

import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.ffa.FFA
import java.util.*

object FFAManager {

    val ffaMatches: MutableList<FFA> = mutableListOf()

    fun load() {
        for (kit in Kit.kits) {
            if (!kit.kitData.ffa) continue

            val ffa = FFA(kit)
            ffaMatches.add(ffa)
        }
    }

    fun getByUUID(uuid: UUID): FFA? {
        return ffaMatches.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun getByKit(kit: Kit): FFA? {
        return ffaMatches.stream().filter { it.kit.name.equals(kit.name, false) }
            .findFirst().orElse(null)
    }
}