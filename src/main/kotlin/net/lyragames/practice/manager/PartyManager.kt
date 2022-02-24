package net.lyragames.practice.manager

import net.lyragames.practice.party.Party
import java.util.*

object PartyManager {

    val parties: MutableList<Party> = mutableListOf()

    fun getByUUID(uuid: UUID): Party? {
        return parties.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }
}