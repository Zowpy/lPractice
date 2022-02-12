package net.lyragames.practice

import net.lyragames.llib.LyraPlugin
import net.lyragames.llib.utils.ConfigFile

class PracticePlugin : LyraPlugin() {

    var kitsFile: ConfigFile? = null
    var arenasFile: ConfigFile? = null

    override fun onEnable() {
        instance = this
        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")
    }

    override fun onDisable() {
        instance = null
    }

    companion object {
        @JvmStatic
        var instance: PracticePlugin? = null
    }
}