package net.lyragames.practice.arena

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.Location

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

open class Arena(val name: String) {
    var l1: Location? = null
    var l2: Location? = null
    var min: Location? = null
    var max: Location? = null

    open var arenaType = ArenaType.NORMAL

    var deadzone = 0
    var free = true

    var duplicate = false

    lateinit var bounds: Cuboid

    open val isSetup: Boolean
        get() = l1 != null && l2 != null && min != null && max != null

    open fun save() {
        val configFile = PracticePlugin.instance.arenasFile
        val configSection = configFile.getConfigurationSection("arenas.$name")

        configSection.set("l1", LocationUtil.serialize(l1))
        configSection.set("l2", LocationUtil.serialize(l2))
        configSection.set("min", LocationUtil.serialize(min))
        configSection.set("max", LocationUtil.serialize(max))
        configSection.set("deadzone", deadzone)
        configSection.set("type", arenaType.name)

        configFile.save()
    }

    open fun delete() {
        val configFile = PracticePlugin.instance.arenasFile

        configFile.config.set("arenas.$name", null)

        configFile.save()
    }

    open fun isFree(): Boolean {
        return free
    }

    open fun duplicate(world: org.bukkit.World, times: Int) {}

    companion object {
        @JvmStatic
        val arenas: MutableList<Arena> = mutableListOf()

        @JvmStatic
        fun getByName(name: String): Arena? {
            return arenas.stream().filter { arena -> arena.name.equals(name, true) }
                .findFirst().orElse(null)
        }
    }
}