package net.lyragames.practice.constants

import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import org.bukkit.Location

object Constants {

    var SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.settingsFile.getString("SPAWN"))
    var FFA_SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("spawn-location"))
    var MIN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("safe-zone.min"))
    var MAX: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("safe-zone.max"))
    var SAFE_ZONE: Cuboid? = null

    fun load() {
        if (MIN != null && MAX != null) {
            SAFE_ZONE = Cuboid(MIN, MAX)
        }
    }
}