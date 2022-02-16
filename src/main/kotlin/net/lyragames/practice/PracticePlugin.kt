package net.lyragames.practice

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.vaperion.blade.Blade
import me.vaperion.blade.command.bindings.impl.BukkitBindings
import me.vaperion.blade.command.bindings.impl.DefaultBindings
import me.vaperion.blade.command.container.impl.BukkitCommandContainer
import net.lyragames.llib.LyraPlugin
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.ArenaProvider
import net.lyragames.practice.command.admin.ArenaCommand
import net.lyragames.practice.command.admin.KitCommand
import net.lyragames.practice.database.PracticeMongo
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.manager.KitManager
import net.lyragames.practice.match.listener.MatchListener
import net.lyragames.practice.profile.ProfileListener

class PracticePlugin : LyraPlugin() {

    private lateinit var settingsFile: ConfigFile
    lateinit var kitsFile: ConfigFile
    lateinit var arenasFile: ConfigFile

    private lateinit var arenaManager: ArenaManager
    private lateinit var kitManager: KitManager

    lateinit var practiceMongo: PracticeMongo

    private lateinit var blade: Blade

    override fun onEnable() {
        instance = this

        settingsFile = ConfigFile(this, "settings")
        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")

        practiceMongo = PracticeMongo(settingsFile.getString("mongodb.uri"))

        arenaManager = ArenaManager()
        arenaManager.load()

        kitManager = KitManager()
        kitManager.load()

        blade = Blade.of()
            .containerCreator(BukkitCommandContainer.CREATOR).binding(BukkitBindings()).binding(DefaultBindings())
            .bind(Arena::class.java, ArenaProvider())
            .build()

        blade
            .register(ArenaCommand())
            .register(KitCommand())

        server.pluginManager.registerEvents(ProfileListener(), this)
        server.pluginManager.registerEvents(MatchListener(), this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: PracticePlugin

        @JvmStatic
        val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .create()
    }
}