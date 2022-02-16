package net.lyragames.practice.manager

import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

class ArenaManager {

    fun load() {
        val configFile = PracticePlugin.instance.arenasFile

        if (configFile.getConfigurationSection("arenas") == null) return

        for (key in configFile.getConfigurationSection("arenas").getKeys(false)) {
            val arena = StandaloneArena(key)
            val section = configFile.getConfigurationSection("arenas.$key")

            if (!section.getString("l1").equals("null", false)) {
                arena.l1 = LocationUtil.deserialize(section.getString("l1"))
            }

            if (!section.getString("l2").equals("null", false)) {
                arena.l2 = LocationUtil.deserialize(section.getString("l2"))
            }

            if (!section.getString("min").equals("null", false)) {
                arena.min = LocationUtil.deserialize(section.getString("min"))
            }

            if (!section.getString("max").equals("null", false)) {
                arena.max = LocationUtil.deserialize(section.getString("l1"))
            }

            if (arena.min != null && arena.max != null) {
                arena.bounds = Cuboid(arena.min, arena.max)
            }

            if (section.getConfigurationSection("duplicates") != null) {
                for (duplicateKey in section.getConfigurationSection("duplicates").getKeys(false)) {
                    val arena1 = Arena(key + duplicateKey)
                    val section1 = section.getConfigurationSection("duplicates.$duplicateKey")

                    arena1.duplicate = true

                    if (!section1.getString("l1").equals("null", false)) {
                        arena1.l1 = LocationUtil.deserialize(section1.getString("l1"))
                    }

                    if (!section1.getString("l2").equals("null", false)) {
                        arena1.l2 = LocationUtil.deserialize(section1.getString("l2"))
                    }

                    if (!section1.getString("min").equals("null", false)) {
                        arena1.min = LocationUtil.deserialize(section1.getString("min"))
                    }

                    if (!section1.getString("max").equals("null", false)) {
                        arena1.max = LocationUtil.deserialize(section1.getString("l1"))
                    }

                    if (arena1.min != null && arena1.max != null) {
                        arena1.bounds = Cuboid(arena1.min, arena1.max)
                    }

                    arena.duplicates.add(arena1)
                }
            }

            Arena.arenas.add(arena)
        }
    }
}