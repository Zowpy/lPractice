package net.lyragames.practice.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World

object LocationHelper {

    fun getLocation(world: World, location: Location): Location {
        return Location(world, location.x, location.y, location.z, location.yaw, location.pitch)
    }

    fun findBedLocations(start: Location): MutableList<Location> {
        val locations = mutableListOf<Location>()

        for (x in start.blockX - 2 until start.blockX + 2) {
            for (y in start.blockY - 2 until start.blockY + 2) {
                for (z in start.blockZ - 2 until start.blockZ + 2) {
                    val loc = Location(start.world, x.toDouble(), y.toDouble(), z.toDouble())

                    if (loc.block.type == Material.BED || loc.block.type == Material.BED_BLOCK) {
                        locations.add(loc)
                    }
                }
            }
        }

        return locations
    }
}