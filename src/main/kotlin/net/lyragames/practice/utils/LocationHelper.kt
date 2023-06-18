package net.lyragames.practice.utils

import org.bukkit.Location
import org.bukkit.World

object LocationHelper {

    fun getLocation(world: World, location: Location): Location {
        return Location(world, location.x, location.y, location.z, location.yaw, location.pitch)
    }
}