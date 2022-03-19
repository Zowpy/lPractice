package net.lyragames.practice.manager

import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.event.map.EventMap
import java.util.concurrent.ThreadLocalRandom

object EventMapManager {

    val maps: MutableList<EventMap> = mutableListOf()

    fun load() {
        val configFile = PracticePlugin.instance.eventsFile

        if (configFile.getConfigurationSection("maps") == null) return

        for (key in configFile.getConfigurationSection("maps").getKeys(false)) {
            if (key == null) continue

            val eventMap = EventMap(key)

            val section = configFile.getConfigurationSection("maps.${key}")

            if (section.getString("spawn") != null) {
                eventMap.spawn = LocationUtil.deserialize(section.getString("spawn"))
            }

            if (section.getString("l1") != null) {
                eventMap.l1 = LocationUtil.deserialize(section.getString("l1"))
            }

            if (section.getString("l2") != null) {
                eventMap.l2 = LocationUtil.deserialize(section.getString("l2"))
            }

            maps.add(eventMap)
        }
    }

    fun getFreeMap(): EventMap? {
        return if (maps.isEmpty()) null else maps[ThreadLocalRandom.current().nextInt(maps.size)]
    }

    fun getByName(name: String): EventMap? {
        return maps.stream().filter { it.name.equals(name, true) }
            .findFirst().orElse(null)
    }
}