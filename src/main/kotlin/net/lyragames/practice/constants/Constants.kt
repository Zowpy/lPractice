package net.lyragames.practice.constants

import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import org.bukkit.Location

object Constants {

    var SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.settingsFile.getString("SPAWN"))
    var FFA_SPAWN: Location? = LocationUtil.deserialize(PracticePlugin.instance.ffaFile.getString("spawn-location"))
}