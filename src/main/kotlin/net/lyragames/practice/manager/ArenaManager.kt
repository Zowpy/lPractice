package net.lyragames.practice.manager

import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.impl.mlgrush.MLGRushArena
import net.lyragames.practice.arena.impl.mlgrush.StandaloneMLGRushArena
import net.lyragames.practice.kit.Kit


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
            var arena = StandaloneArena(key)

            val section = configFile.getConfigurationSection("arenas.$key")

            if (section.getString("type").equals("MLGRUSH", true)) {
                arena = StandaloneMLGRushArena(key)
            }else if (section.getString("type").equals("BEDFIGHT")) {
                arena = StandaloneBedWarsArena(key)
            }

            arena.deadzone = section.getInt("deadzone")
            arena.arenaType = ArenaType.valueOf(section.getString("type").uppercase())

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

         //   if (!section.getString("arenaType").equals("null", false)) {
          //      arena.arenaType = ArenaType.valueOf(section.getString("arenaType"))
          //  }

            //if (!section.getDouble("deadzone").equals("null")) {
            //    arena.deadzone = section.getDouble("deadzone")
          //  }

            if (arena.min != null && arena.max != null) {
                arena.bounds = Cuboid(arena.min, arena.max)
            }

            if (arena is StandaloneMLGRushArena) {

                if (!section.getString("bed1").equals("null", false)) {
                    arena.bed1 = LocationUtil.deserialize(section.getString("bed1"))
                }

                if (!section.getString("bed2").equals("null", false)) {
                    arena.bed2 = LocationUtil.deserialize(section.getString("bed2"))
                }
            }

            if (arena is StandaloneBedWarsArena) {
                if (!section.getString("redSpawn").equals("null", false)) {
                    arena.redSpawn = LocationUtil.deserialize(section.getString("redSpawn"))
                }

                if (!section.getString("blueSpawn").equals("null", false)) {
                    arena.blueSpawn = LocationUtil.deserialize(section.getString("blueSpawn"))
                }

                if (!section.getString("redBed").equals("null", false)) {
                    arena.redBed = LocationUtil.deserialize(section.getString("redBed"))
                }

                if (!section.getString("blueBed").equals("null", false)) {
                    arena.blueBed = LocationUtil.deserialize(section.getString("blueBed"))
                }
            }

            if (section.getConfigurationSection("duplicates") != null) {
                for (duplicateKey in section.getConfigurationSection("duplicates").getKeys(false)) {
                    var arena1 = Arena("$key$duplicateKey")
                    val section1 = section.getConfigurationSection("duplicates.$duplicateKey")

                    if (arena is StandaloneMLGRushArena) {
                        arena1 = MLGRushArena("$key$duplicateKey")
                    }

                    arena1.duplicate = true

                    if (arena1 is BedWarsArena) {
                        if (!section1.getString("redSpawn").equals("null", false)) {
                            arena1.redSpawn = LocationUtil.deserialize(section1.getString("redSpawn"))
                        }

                        if (!section1.getString("blueSpawn").equals("null", false)) {
                            arena1.blueSpawn = LocationUtil.deserialize(section1.getString("blueSpawn"))
                        }

                        if (!section1.getString("redBed").equals("null", false)) {
                            arena1.redBed = LocationUtil.deserialize(section1.getString("redBed"))
                        }

                        if (!section1.getString("blueBed").equals("null", false)) {
                            arena1.blueBed = LocationUtil.deserialize(section1.getString("blueBed"))
                        }

                        arena.duplicates.add(arena1)

                        continue
                    }

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

                    if (arena1 is MLGRushArena) {
                        if (!section1.getString("bed1").equals("null", false)) {
                            arena1.bed1 = LocationUtil.deserialize(section1.getString("bed1"))
                        }

                        if (!section1.getString("bed2").equals("null", false)) {
                            arena1.bed2 = LocationUtil.deserialize(section1.getString("bed2"))
                        }
                    }

                    arena.duplicates.add(arena1)
                }
            }

            Arena.arenas.add(arena)
        }
    }

    fun getFreeArena(): Arena? {
        return Arena.arenas
            .stream().filter { !it.duplicate && it.isSetup && it.isFree()}
            .findAny().orElse(null)
    }

    fun getFreeArena(kit: Kit): Arena? {
     /*   return Arena.arenas
            .stream().filter { !it.duplicate && it.isSetup && it.isFree() && (kit.kitData.sumo && it.arenaType == ArenaType.SUMO)}
            .findAny().orElse(null) */

        for (arena in Arena.arenas) {
            if (arena == null) continue
            if (arena.duplicate && !arena.isSetup && !arena.isFree()) continue

            if (kit.kitData.sumo && arena.arenaType != ArenaType.SUMO) continue
            if (kit.kitData.mlgRush && arena.arenaType != ArenaType.MLGRUSH) continue
            if (kit.kitData.bedFights && arena.arenaType != ArenaType.BEDFIGHT) continue

            return arena
        }

        return null
    }
}