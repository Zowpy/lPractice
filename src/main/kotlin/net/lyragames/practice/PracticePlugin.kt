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
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.llib.utils.InventoryUtil
import net.lyragames.menu.MenuAPI
import net.lyragames.practice.adapter.ScoreboardAdapter
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.ArenaProvider
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.arena.type.ArenaTypeProvider
import net.lyragames.practice.command.*
import net.lyragames.practice.command.admin.*
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.database.Mongo
import net.lyragames.practice.database.MongoCredentials
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.map.EventMapProvider
import net.lyragames.practice.event.map.type.EventMapType
import net.lyragames.practice.event.map.type.EventMapTypeProvider
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.KitProvider
import net.lyragames.practice.kit.editor.listener.KitEditorListener
import net.lyragames.practice.kit.serializer.EditKitSerializer
import net.lyragames.practice.manager.*
import net.lyragames.practice.match.listener.MatchListener
import net.lyragames.practice.profile.ProfileListener
import net.lyragames.practice.queue.task.QueueTask
import net.lyragames.practice.task.EnderPearlCooldownTask
import net.lyragames.practice.task.EventAnnounceTask
import net.lyragames.practice.task.TNTEventBlockRemovalTask
import net.lyragames.practice.task.TNTTagTask
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class PracticePlugin : LyraPlugin() {

    lateinit var settingsFile: ConfigFile
    lateinit var kitsFile: ConfigFile
    lateinit var arenasFile: ConfigFile
    lateinit var scoreboardFile: ConfigFile
    lateinit var ffaFile: ConfigFile
    lateinit var eventsFile: ConfigFile

    lateinit var practiceMongo: Mongo

    private lateinit var blade: Blade

    override fun onEnable() {
        instance = this

        settingsFile = ConfigFile(this, "settings")
        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")
        scoreboardFile = ConfigFile(this, "scoreboard")
        ffaFile = ConfigFile(this, "ffa")
        eventsFile = ConfigFile(this, "events")
        logger.info("Successfully loaded files!")

        loadMongo()
        cleanupWorld()
        InventoryUtil.removeCrafting(Material.WORKBENCH)

        CC.PRIMARY = ChatColor.valueOf(settingsFile.getString("COLOR.PRIMARY")).toString()
        CC.SECONDARY = ChatColor.valueOf(settingsFile.getString("COLOR.SECONDARY")).toString()

        ArenaManager.load()
        logger.info("Successfully loaded ${if (Arena.arenas.size == 1) "1 arena!" else "${Arena.arenas.size} arenas!"}")

        KitManager.load()
        logger.info("Successfully loaded ${if (Kit.kits.size == 1) "1 kit!" else "${Kit.kits.size} kits!"}")

        QueueManager.load()

        EventMapManager.load()
        logger.info("Successfully loaded ${if (EventMapManager.maps.size == 1) "1 event map!" else "${EventMapManager.maps.size} event maps!"}")

        FFAManager.load()

        MenuAPI(this)

        blade = Blade.of()
            .containerCreator(BukkitCommandContainer.CREATOR).binding(BukkitBindings()).binding(DefaultBindings())
            .bind(Arena::class.java, ArenaProvider).bind(Kit::class.java, KitProvider).bind(EventMap::class.java, EventMapProvider).bind(EventMapType::class.java, EventMapTypeProvider)
            .bind(ArenaType::class.java, ArenaTypeProvider)
            .build()

        blade
            .register(ArenaCommand)
            .register(KitCommand)
            .register(SetSpawnCommand)
            .register(DuelCommand)
            .register(LeaveCommand)
            .register(MatchSnapshotCommand)
            .register(PartyCommand)
            .register(EventMapCommand)
            .register(EventCommand)
            .register(SpectateCommand)
            .register(SettingsCommand)
            .register(FollowCommand)
            .register(FFACommand)

        Constants.load()

        QueueTask
        EventAnnounceTask
        TNTEventBlockRemovalTask
        TNTTagTask
        EnderPearlCooldownTask

        if (scoreboardFile.getBoolean("scoreboard.enabled")) {
            Assemble(this, ScoreboardAdapter(scoreboardFile))
        }

        server.pluginManager.registerEvents(ProfileListener, this)
        server.pluginManager.registerEvents(MatchListener, this)
        server.pluginManager.registerEvents(KitEditorListener, this)
        server.pluginManager.registerEvents(ItemListener(), this)
    }

    private fun loadMongo() {
        try {
            val builder = MongoCredentials.Builder()
                .host(settingsFile.getString("MONGODB.NORMAL.HOST"))
                .port(settingsFile.getInt("MONGODB.NORMAL.PORT"))

            if (settingsFile.getBoolean("MONGODB.NORMAL.AUTH.ENABLED")) {
                builder.username(settingsFile.getString("MONGODB.NORMAL.AUTH.USERNAME"))
                builder.password(settingsFile.getString("MONGODB.NORMAL.AUTH.PASSWORD"))
            }
            practiceMongo = Mongo(settingsFile.getString("MONGODB.NORMAL.AUTH.AUTH-DATABASE"))
            practiceMongo.load(builder.build())

            logger.info("Successfully connected MongoDB!")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.severe("Failed to connect MongoDB!")
        }
    }

    private fun cleanupWorld() {
        for (world in server.worlds) {
            world.time = 4000

            for (entity in world.entities) {
                if (entity is Player) {
                    continue;
                }
                if (entity is LivingEntity || entity is Item || entity is ExperienceOrb) {
                    entity.remove()
                }
            }
        }

        logger.info("Cleaned all worlds")
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