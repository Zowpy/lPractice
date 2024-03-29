package net.lyragames.practice.constants

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.LocationUtil
import org.bukkit.Location

object Constants {

    var SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.settingsFile.getString("SPAWN"))
    var FFA_SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SPAWN"))
    var MIN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SAFE-ZONE.MIN"))
    var MAX: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("SAFE-ZONE.MAX"))
    var SAFE_ZONE: Cuboid? = null

    fun load() {
        if (MIN != null && MAX != null) {
            SAFE_ZONE = Cuboid(MIN!!, MAX!!)
        }
    }
}