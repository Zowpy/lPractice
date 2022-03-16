package net.lyragames.practice.manager

import net.lyragames.practice.events.map.EventMap

object EventMapManager {

    val maps: MutableList<EventMap> = mutableListOf()

    fun getByName(name: String): EventMap? {
        return maps.stream().filter { it.name.equals(name, true) }
            .findFirst().orElse(null)
    }
}