package net.lyragames.practice

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.PaperCommandManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.thatkawaiisam.assemble.Assemble
import me.zowpy.menu.MenuAPI
import net.lyragames.practice.adapter.ScoreboardAdapter
import net.lyragames.practice.api.PracticeAPI
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.command.*
import net.lyragames.practice.command.admin.*
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.database.Mongo
import net.lyragames.practice.database.MongoCredentials
import net.lyragames.practice.duel.DuelRequest
import net.lyragames.practice.duel.gson.DuelRequestGsonAdapter
import net.lyragames.practice.event.listener.EventListener
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.map.type.EventMapType
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.kit.editor.listener.KitEditorListener
import net.lyragames.practice.kit.serializer.EditKitSerializer
import net.lyragames.practice.listener.MoveListener
import net.lyragames.practice.listener.PreventionListener
import net.lyragames.practice.listener.WorldListener
import net.lyragames.practice.manager.*
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.ffa.listener.FFAListener
import net.lyragames.practice.match.listener.MatchListener
import net.lyragames.practice.profile.ProfileListener
import net.lyragames.practice.queue.task.QueueTask
import net.lyragames.practice.task.*
import net.lyragames.practice.utils.ConfigFile
import net.lyragames.practice.utils.InventoryUtil
import net.lyragames.practice.utils.item.ItemListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.defaults.WhitelistCommand
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class PracticePlugin : JavaPlugin() {

    lateinit var settingsFile: ConfigFile
    lateinit var tablistFile: ConfigFile
    lateinit var kitsFile: ConfigFile
    lateinit var arenasFile: ConfigFile
    lateinit var scoreboardFile: ConfigFile
    lateinit var ffaFile: ConfigFile
    lateinit var eventsFile: ConfigFile
    lateinit var languageFile: ConfigFile

    lateinit var commandAPI: PaperCommandManager

    lateinit var API: PracticeAPI

    lateinit var practiceMongo: Mongo

    //private lateinit var ziggurat: Ziggurat
    override fun onEnable() {
        instance = this

        settingsFile = ConfigFile(this, "settings")
        tablistFile = ConfigFile(this, "tablist")
        kitsFile = ConfigFile(this, "kits")
        arenasFile = ConfigFile(this, "arenas")
        scoreboardFile = ConfigFile(this, "scoreboard")
        ffaFile = ConfigFile(this, "ffa")
        eventsFile = ConfigFile(this, "events")
        languageFile = ConfigFile(this, "language")

        logger.info("Successfully loaded files!")

        loadMongo()
        cleanupWorld()

        InventoryUtil.removeCrafting(Material.WORKBENCH)


        ArenaManager.load()
        logger.info("Successfully loaded ${if (Arena.arenas.size == 1) "1 arena!" else "${Arena.arenas.size} arenas!"}")

        KitManager.load()
        logger.info("Successfully loaded ${if (Kit.kits.size == 1) "1 kit!" else "${Kit.kits.size} kits!"}")

        QueueManager.load()

        EventMapManager.load()
        logger.info("Successfully loaded ${if (EventMapManager.maps.size == 1) "1 event map!" else "${EventMapManager.maps.size} event maps!"}")

        FFAManager.load()
        ArenaRatingManager.load()

        API = PracticeAPI()

        MenuAPI(this)

        commandAPI = PaperCommandManager(this)

        commandAPI.commandContexts.registerContext(Player::class.java) {
            val source: String = it.popFirstArg()
            if (it.getIssuer().isPlayer() && (source.equals("self", ignoreCase = true) || source == "")) {
                return@registerContext it.getPlayer()
            }
            if (!it.getIssuer().isPlayer() && (source.equals("self", ignoreCase = true) || source == "")) {
                throw InvalidCommandArgument("You cannot do this!")
            }

            return@registerContext Bukkit.getPlayer(source)
                ?: throw InvalidCommandArgument("That player is offline!")
        }

        commandAPI.commandContexts.registerContext(Arena::class.java) {
            val source: String = it.popFirstArg()
            return@registerContext source.let { Arena.getByName(source) } ?: throw InvalidCommandArgument(Locale.CANT_FIND_ARENA.getMessage())

        }

        commandAPI.commandContexts.registerContext(Kit::class.java) {
            val source: String = it.popFirstArg()
            return@registerContext source.let { Kit.getByName(source) } ?: throw InvalidCommandArgument("Cant find Kit!")

        }


        commandAPI.commandContexts.registerContext(EventMap::class.java) {
            val source: String = it.popFirstArg()
            return@registerContext source.let { EventMapManager.getByName(source) } ?: throw InvalidCommandArgument("Cant find event map!")

        }

        commandAPI.commandContexts.registerContext(EventMapType::class.java) {
            val source: String = it.popFirstArg()
            return@registerContext source.let { EventMapType.valueOf(source) }

        }


        commandAPI.commandContexts.registerContext(ArenaType::class.java) {
            val source: String = it.popFirstArg()
            return@registerContext source.let { ArenaType.valueOf(source) }

        }



        arrayOf(DuelCommand, EventCommand, LeaveCommand, MatchSnapshotCommand, PartyCommand, SettingsCommand, SpawnCommand, SpectateCommand, ArenaCommand, ArenaRatingCommand, BuildCommand, EventMapCommand, FFACommand, FollowCommand, KitCommand, SetSpawnCommand).forEach { commandAPI.registerCommand(
            it as BaseCommand?
        ) }

        commandAPI.enableUnstableAPI("help")





        /*
        CommandAPI(this)
            .bind(Arena::class.java, ArenaProvider)
            .bind(Kit::class.java, KitProvider)
            .bind(EventMap::class.java, EventMapProvider)
            .bind(EventMapType::class.java, EventMapTypeProvider)
            .bind(ArenaType::class.java, ArenaTypeProvider)
            .beginCommandRegister()
            .register(ArenaCommand)
            .register(KitCommand)
            .register(SetSpawnCommand)
            .register(DuelCommand)
            .register(LeaveCommand)
            .register(MatchSnapshotCommand)
            .register(EventMapCommand)
            .register(EventCommand)
            .register(SpectateCommand)
            .register(SettingsCommand)
            .register(FollowCommand)
            .register(FFACommand)
            .register(RateMapCommand)
            .register(BuildCommand)
            .register(ArenaRatingCommand)
            .register(SpawnCommand)
            .register(PartyCommand)
            .endRegister()

         */

        Constants.load()

        QueueTask
        EventAnnounceTask
        TNTEventBlockRemovalTask
        TNTTagTask
        MatchSnapshotExpireTask
        EnderPearlCooldownTask
        ArrowCooldownTask
        FFAItemClearTask

        //EntityHider(this, EntityHider.Policy.WHITELIST).init()

        if (tablistFile.getBoolean("tablist.enabled")) {
            //ziggurat = Ziggurat(this, TablistAdapter())
        }

        if (scoreboardFile.getBoolean("scoreboard.enabled")) {
            Assemble(this, ScoreboardAdapter(scoreboardFile))
        }

        server.pluginManager.registerEvents(WorldListener, this)
        server.pluginManager.registerEvents(ProfileListener, this)
        server.pluginManager.registerEvents(MatchListener, this)
        server.pluginManager.registerEvents(FFAListener, this)
        server.pluginManager.registerEvents(EventListener, this)
        server.pluginManager.registerEvents(KitEditorListener, this)
        server.pluginManager.registerEvents(PreventionListener, this)
        server.pluginManager.registerEvents(MoveListener, this)
        server.pluginManager.registerEvents(ItemListener(), this)

    }

    override fun onDisable() {
        for (match in Match.matches.elements()) {
            match!!.reset()
        }
        commandAPI.unregisterCommands()

    }

    private fun loadMongo() {
        try {
            val builder = MongoCredentials.Builder()
                .host(settingsFile.getString("MONGODB.NORMAL.HOST"))
                .port(settingsFile.getInt("MONGODB.NORMAL.PORT"))
                .uri(settingsFile.getString("MONGODB.URI.CONNECTION-STRING"))
                .useUri(settingsFile.getBoolean("MONGODB.URI-MODE"))
                .database(settingsFile.config.getString("MONGODB.DATABASE", "lpractice"))

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
                    continue
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
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeHierarchyAdapter(EditedKit::class.java, EditKitSerializer)
            .registerTypeHierarchyAdapter(DuelRequest::class.java, DuelRequestGsonAdapter)
            .create()
    }
}