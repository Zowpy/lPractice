package net.lyragames.practice.profile

import com.boydti.fawe.logging.LoggingChangeSet.api
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.duel.DuelRequest
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.party.invitation.PartyInvitation
import net.lyragames.practice.profile.editor.KitEditorData
import net.lyragames.practice.profile.settings.Settings
import net.lyragames.practice.profile.statistics.KitStatistic
import net.lyragames.practice.profile.statistics.global.GlobalStatistics
import net.lyragames.practice.queue.QueuePlayer
import net.lyragames.practice.utils.Cooldown
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit


/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class Profile(val uuid: UUID, var name: String?) {

    var match: UUID? = null
    var matchObject: Match? = null
    var ffa: UUID? = null
    var spectatingMatch: UUID? = null

    var queuePlayer: QueuePlayer? = null

    var kitStatistics: MutableList<KitStatistic> = mutableListOf()
    var globalStatistic = GlobalStatistics()

    var party: UUID? = null

    var partyInvites: MutableList<PartyInvitation> = mutableListOf()
    var duelRequests: MutableList<DuelRequest> = mutableListOf()

    var kitEditorData: KitEditorData? = KitEditorData()
    var settings: Settings = Settings()
    
    val followers: MutableList<UUID> = mutableListOf()

    var silent = false
    var following = false

    var state = ProfileState.LOBBY

    var canBuild = false
    var enderPearlCooldown: Cooldown? = null
    var arrowCooldown: Cooldown? = null
    var fireBallCooldown: Cooldown? = null

    val player: Player
        get() = Bukkit.getPlayer(uuid)

    private fun toBson() : Document {
        return Document("_id", uuid.toString())
            .append("name", name)
            .append("duelRequests", duelRequests.map { PracticePlugin.GSON.toJson(it) }.toMutableList())
            .append("partyInvites", partyInvites.map { PracticePlugin.GSON.toJson(it) }.toMutableList())
            .append("kitsStatistics", kitStatistics.map { PracticePlugin.GSON.toJson(it) }.toMutableList())
            .append("globalStatistics", PracticePlugin.GSON.toJson(globalStatistic))
            .append("settings", PracticePlugin.GSON.toJson(settings))
            .append("silent", silent)
    }

    fun save() {
        CompletableFuture.runAsync {
            PracticePlugin.instance.practiceMongo.profiles.updateOne(Filters.eq("uuid", uuid.toString()), Document("${'$'}set", toBson()), UpdateOptions().upsert(true))
        }
    }

    fun saveSync() {
        PracticePlugin.instance.practiceMongo.profiles.updateOne(Filters.eq("uuid", uuid.toString()), Document("${'$'}set", toBson()), UpdateOptions().upsert(true))
    }

    fun load() {
        val document = PracticePlugin.instance.practiceMongo.profiles.find(Filters.eq("uuid", uuid.toString())).first()

        if (document == null) {
            for (kit in Kit.kits) {
                val kitStatistic = KitStatistic(kit.name)
                kitStatistics.add(kitStatistic)
            }
            save()
            return
        }

        load(document)
    }

    fun getPartyInvite(uuid: UUID): PartyInvitation? {
        return partyInvites.stream().filter { it.uuid == uuid && !it.isExpired() }
            .findFirst().orElse(null)
    }

    fun getDuelRequest(uuid: UUID): DuelRequest? {
        return duelRequests.stream().filter { it.uuid == uuid && !it.isExpired() }
            .findFirst().orElse(null)
    }

    fun getKitStatistic(kit: String): KitStatistic? {
        return kitStatistics.stream().filter { kitStatistic -> kitStatistic.kit.equals(kit, true) }
            .findFirst().orElse(null)
    }

    fun load(document: Document) {
        var save = false

        if (name != null && !document.getString("name").equals(name)) {
            save = true
        }else if (name == null) {
            name = document.getString("name")
        }

        duelRequests = document.getList("duelRequests", String::class.java).map { PracticePlugin.GSON.fromJson(it, DuelRequest::class.java) }.toMutableList()
        partyInvites = document.getList("partyInvites", String::class.java).map { PracticePlugin.GSON.fromJson(it, PartyInvitation::class.java) }.toMutableList()

        if (duelRequests.removeIf { it.isExpired() } || partyInvites.removeIf { it.isExpired() }) {
            save = true
        }

        kitStatistics = document.getList("kitsStatistics", String::class.java).map { PracticePlugin.GSON.fromJson(it, KitStatistic::class.java) }.toMutableList()
        globalStatistic = PracticePlugin.GSON.fromJson(document.getString("globalStatistics"), GlobalStatistics::class.java)
        settings = PracticePlugin.GSON.fromJson(document.getString("settings"), Settings::class.java)

        silent = document.getBoolean("silent")

        for (kit in Kit.kits) {
            var found = false

            for (kitStatistic in kitStatistics) {
                if (kitStatistic.kit.equals(kit.name, false)) {
                    found = true
                    break
                }
            }

            if (!found) {
                val kitStatistic = KitStatistic(kit.name)
                kitStatistics.add(kitStatistic)
                save = true
            }
        }

        if (save) {
            save()
        }
    }

    companion object {
        @JvmStatic
        val profiles: ConcurrentHashMap<UUID, Profile> = ConcurrentHashMap<UUID, Profile>()



        @JvmStatic
        fun getByUUID(uuid: UUID): Profile? {
            return profiles[uuid]//profiles.stream().filter { profile: Profile? -> profile?.uuid == uuid }
                //.findFirst().orElse(null)
        }
    }
}