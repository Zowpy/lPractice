package net.lyragames.practice

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.thatkawaiisam.assemble.Assemble
import me.vaperion.blade.Blade
import me.vaperion.blade.command.bindings.impl.BukkitBindings
import me.vaperion.blade.command.bindings.impl.DefaultBindings
import me.vaperion.blade.command.container.impl.BukkitCommandContainer
import net.lyragames.llib.LyraPlugin
import net.lyragames.llib.item.ItemListener
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.menu.MenuAPI
import net.lyragames.practice.adapter.ScoreboardAdapter
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.ArenaProvider
import net.lyragames.practice.command.DuelCommand
import net.lyragames.practice.command.LeaveCommand
import net.lyragames.practice.command.MatchSnapshotCommand
import net.lyragames.practice.command.PartyCommand
import net.lyragames.practice.command.admin.ArenaCommand
import net.lyragames.practice.command.admin.KitCommand
import net.lyragames.practice.command.admin.SetSpawnCommand
import net.lyragames.practice.database.PracticeMongo
import net.lyragames.practice.entity.EntityHider
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.KitProvider
import net.lyragames.practice.kit.editor.listener.KitEditorListener
import net.lyragames.practice.kit.serializer.EditKitSerializer
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.manager.KitManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.listener.MatchListener
import net.lyragames.practice.profile.ProfileListener
import net.lyragames.practice.queue.task.QueueTask
import net.lyragames.practice.task.MatchSnapshotExpireTask


class PracticePlugin : LyraPlugin() {

    lateinit var settingsFile: ConfigFile
    lateinit var kitsFile: ConfigFile
    lateinit var arenasFile: ConfigFile
    lateinit var scoreboardFile: ConfigFile
    lateinit var ffaFile: ConfigFile

    lateinit var arenaManager: ArenaManager
    private lateinit var kitManager: KitManager
    lateinit var queueManager: QueueManager

    lateinit var practiceMongo: PracticeMongo

    private lateinit var blade: Blade

    override fun onEnable() {
        instance = this

        settingsFile = ConfigFile(this, "settings")
        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")
        scoreboardFile = ConfigFile(this, "scoreboard")
        ffaFile = ConfigFile(this, "ffa")

        practiceMongo = PracticeMongo(settingsFile.getString("mongodb.uri"))

        arenaManager = ArenaManager
        arenaManager.load()

        kitManager = KitManager
        kitManager.load()

        queueManager = QueueManager
        queueManager.load()

        MenuAPI(this)

        blade = Blade.of()
            .containerCreator(BukkitCommandContainer.CREATOR).binding(BukkitBindings()).binding(DefaultBindings())
            .bind(Arena::class.java, ArenaProvider).bind(Kit::class.java, KitProvider)
            .build()

        val hider = EntityHider(this, EntityHider.Policy.BLACKLIST)
        hider.init()

        blade
            .register(ArenaCommand)
            .register(KitCommand)
            .register(PartyCommand)
            .register(MatchSnapshotCommand)
            .register(LeaveCommand)
            .register(SetSpawnCommand)
            .register(DuelCommand)

        QueueTask
        MatchSnapshotExpireTask

        if (scoreboardFile.getBoolean("scoreboard.enabled")) {
            Assemble(this, ScoreboardAdapter(scoreboardFile))
        }

        server.pluginManager.registerEvents(ProfileListener, this)
        server.pluginManager.registerEvents(MatchListener, this)
        server.pluginManager.registerEvents(KitEditorListener, this)
        server.pluginManager.registerEvents(ItemListener(), this)
    }

    companion object {
        @JvmStatic
        lateinit var instance: PracticePlugin

        @JvmStatic
        val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeHierarchyAdapter(EditedKit::class.java, EditKitSerializer)
            .create()
    }
}