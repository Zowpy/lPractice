package net.lyragames.practice

import net.lyragames.llib.LyraPlugin
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.practice.match.listener.MatchListener

class PracticePlugin : LyraPlugin() {

    var kitsFile: ConfigFile? = null
    var arenasFile: ConfigFile? = null

    override fun onEnable() {
        instance = this

        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")

        server.pluginManager.registerEvents(MatchListener(), this)
    }

    override fun onDisable() {
        instance = null
    }

    companion object {
        @JvmStatic
        var instance: PracticePlugin? = null
    }
}