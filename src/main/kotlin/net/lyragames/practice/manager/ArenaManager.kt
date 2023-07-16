package net.lyragames.practice.manager

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.impl.bridge.BridgeArena
import net.lyragames.practice.arena.impl.bridge.StandaloneBridgeArena
import net.lyragames.practice.arena.impl.fireball.FireBallFightArena
import net.lyragames.practice.arena.impl.fireball.StandaloneFireBallFightArena
import net.lyragames.practice.arena.impl.mlgrush.MLGRushArena
import net.lyragames.practice.arena.impl.mlgrush.StandaloneMLGRushArena
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.LocationUtil

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

object ArenaManager {

    fun load() {
        val configFile = PracticePlugin.instance.arenasFile

        if (configFile.getConfigurationSection("arenas") == null) return

        for (key in configFile.getConfigurationSection("arenas").getKeys(false)) {
            val section = configFile.getConfigurationSection("arenas.$key")

            val arena = when(ArenaType.valueOf(section.getString("type").uppercase())) {
                ArenaType.MLGRUSH -> StandaloneMLGRushArena(key)
                ArenaType.BEDFIGHT -> StandaloneBedWarsArena(key)
                ArenaType.BRIDGE -> StandaloneBridgeArena(key)
                ArenaType.FIREBALL_FIGHT -> StandaloneFireBallFightArena(key)
                else -> StandaloneArena(key)
            }

            arena.deadzone = section.getInt("deadzone")
            arena.arenaType = ArenaType.valueOf(section.getString("type").uppercase())

            arena.l1 = LocationUtil.deserialize(section.getString("l1"))
            arena.l2 = LocationUtil.deserialize(section.getString("l2"))
            arena.min = LocationUtil.deserialize(section.getString("min"))
            arena.max = LocationUtil.deserialize(section.getString("max"))

            if (arena.min != null && arena.max != null) {
                arena.bounds = Cuboid(arena.min!!, arena.max!!)
            }

            if (arena is StandaloneMLGRushArena) {
                arena.bed1 = LocationUtil.deserialize(section.getString("bed1"))
                arena.bed2 = LocationUtil.deserialize(section.getString("bed2"))
            }

            if (arena is StandaloneBedWarsArena) {
                arena.redSpawn = LocationUtil.deserialize(section.getString("redSpawn"))
                arena.blueSpawn = LocationUtil.deserialize(section.getString("blueSpawn"))
                arena.redBed = LocationUtil.deserialize(section.getString("redBed"))
                arena.blueBed = LocationUtil.deserialize(section.getString("blueBed"))
            }

            if (arena is StandaloneFireBallFightArena) {
                arena.redSpawn = LocationUtil.deserialize(section.getString("redSpawn"))
                arena.blueSpawn = LocationUtil.deserialize(section.getString("blueSpawn"))
                arena.redBed = LocationUtil.deserialize(section.getString("redBed"))
                arena.blueBed = LocationUtil.deserialize(section.getString("blueBed"))
            }

            if (arena is StandaloneBridgeArena) {
                arena.redSpawn = LocationUtil.deserialize(section.getString("redSpawn"))
                arena.blueSpawn = LocationUtil.deserialize(section.getString("blueSpawn"))

                arena.redPortal1 = LocationUtil.deserialize(section.getString("redPortal1"))
                arena.redPortal2 = LocationUtil.deserialize(section.getString("redPortal2"))

                arena.bluePortal1 = LocationUtil.deserialize(section.getString("bluePortal1"))
                arena.bluePortal2 = LocationUtil.deserialize(section.getString("bluePortal2"))

                arena.bluePortal = Cuboid(arena.bluePortal1!!, arena.bluePortal2!!)
                arena.redPortal = Cuboid(arena.redPortal1!!, arena.redPortal2!!)
            }

            if (section.getConfigurationSection("duplicates") != null) {
                for (duplicateKey in section.getConfigurationSection("duplicates").getKeys(false)) {
                    var arena1 = Arena("$key$duplicateKey")
                    val section1 = section.getConfigurationSection("duplicates.$duplicateKey")

                    if (arena is StandaloneMLGRushArena) {
                        arena1 = MLGRushArena("$key$duplicateKey")
                    }

                    arena1.min = LocationUtil.deserialize(section1.getString("min"))
                    arena1.max = LocationUtil.deserialize(section1.getString("max"))

                    if (arena1.min != null && arena1.max != null) {
                        arena1.bounds = Cuboid(arena1.min!!, arena1.max!!)
                    }

                    arena1.duplicate = true

                    if (arena1 is BedWarsArena) {
                        arena1.redSpawn = LocationUtil.deserialize(section1.getString("redSpawn"))
                        arena1.blueSpawn = LocationUtil.deserialize(section1.getString("blueSpawn"))
                        arena1.redBed = LocationUtil.deserialize(section1.getString("redBed"))
                        arena1.blueBed = LocationUtil.deserialize(section1.getString("blueBed"))

                        arena.duplicates.add(arena1)
                        continue
                    }

                    if (arena1 is FireBallFightArena) {
                        arena1.redSpawn = LocationUtil.deserialize(section1.getString("redSpawn"))
                        arena1.blueSpawn = LocationUtil.deserialize(section1.getString("blueSpawn"))
                        arena1.redBed = LocationUtil.deserialize(section1.getString("redBed"))
                        arena1.blueBed = LocationUtil.deserialize(section1.getString("blueBed"))

                        arena.duplicates.add(arena1)
                        continue
                    }

                    if (arena1 is BridgeArena) {
                        arena1.redSpawn = LocationUtil.deserialize(section1.getString("redSpawn"))
                        arena1.blueSpawn = LocationUtil.deserialize(section1.getString("blueSpawn"))

                        arena1.redPortal1 = LocationUtil.deserialize(section1.getString("redPortal1"))
                        arena1.redPortal2 = LocationUtil.deserialize(section1.getString("redPortal2"))

                        arena1.bluePortal1 = LocationUtil.deserialize(section1.getString("bluePortal1"))
                        arena1.bluePortal2 = LocationUtil.deserialize(section1.getString("bluePortal2"))

                        arena1.bluePortal = Cuboid(arena1.bluePortal1!!, arena1.bluePortal2!!)
                        arena1.redPortal = Cuboid(arena1.redPortal1!!, arena1.redPortal2!!)

                        arena.duplicates.add(arena1)
                        continue
                    }

                    arena1.l1 = LocationUtil.deserialize(section1.getString("l1"))
                    arena1.l2 = LocationUtil.deserialize(section1.getString("l2"))

                    if (arena1 is MLGRushArena) {
                        arena1.bed1 = LocationUtil.deserialize(section1.getString("bed1"))
                        arena1.bed2 = LocationUtil.deserialize(section1.getString("bed2"))
                    }

                    arena.duplicates.add(arena1)
                }
            }

            Arena.arenas.add(arena)
        }
    }

    fun getFreeArena(kit: Kit): Arena? {
     /*   return Arena.arenas
            .stream().filter { !it.duplicate && it.isSetup && it.isFree() && (kit.kitData.sumo && it.arenaType == ArenaType.SUMO)}
            .findAny().orElse(null) */

        for (arena in Arena.arenas) {
            if (!arena.isSetup) continue
            if (kit.kitData.build && !arena.isFree()) continue

            if (kit.kitData.sumo && arena.arenaType != ArenaType.SUMO) continue
            if (kit.kitData.mlgRush && arena.arenaType != ArenaType.MLGRUSH) continue
            if (kit.kitData.bedFights && arena.arenaType != ArenaType.BEDFIGHT) continue
            if (kit.kitData.bridge && arena.arenaType != ArenaType.BRIDGE) continue
            if (kit.kitData.fireballFight && arena.arenaType != ArenaType.FIREBALL_FIGHT) continue

            return arena
        }

        return null
    }
}