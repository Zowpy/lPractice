package net.lyragames.practice.match

import com.boydti.fawe.bukkit.chat.FancyMessage
import com.google.common.base.Joiner
import net.lyragames.practice.Locale
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.ArenaRatingManager
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.StatisticManager
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.spectator.MatchSpectator
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.PlayerUtil
import net.lyragames.practice.utils.TextBuilder
import net.lyragames.practice.utils.TimeUtil
import net.lyragames.practice.utils.countdown.ICountdown
import net.lyragames.practice.utils.countdown.TitleCountdown
import net.lyragames.practice.utils.item.CustomItemStack
import net.lyragames.practice.utils.title.TitleBar
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

open class Match(val kit: Kit, val arena: Arena, val ranked: Boolean) {

    val uuid = UUID.randomUUID()
    var friendly = false
    var matchState = MatchState.STARTING
    var started = 0L
    val players: MutableList<MatchPlayer> = mutableListOf()
    val blocksPlaced: MutableList<Block> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()
    val snapshots: MutableList<MatchSnapshot> = mutableListOf()
    val spectators: MutableList<MatchSpectator> = mutableListOf()
    val countdowns: MutableList<ICountdown> = mutableListOf()
    val rematchingPlayers: MutableList<UUID> = mutableListOf()

    open fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            CustomItemStack.customItemStacks.removeIf { it.uuid == matchPlayer.uuid }

            if (kit.kitData.boxing || kit.kitData.combo) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
            }

            if (kit.kitData.combo) {
                player.noDamageTicks = 3
                player.maximumNoDamageTicks = 3
            }

            PlayerUtil.reset(player)
            PlayerUtil.denyMovement(player)

            player.teleport(matchPlayer.spawn)
            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            countdowns.add(TitleCountdown(
                player,
                "${CC.SECONDARY}<seconds>${CC.PRIMARY}...",
                "${CC.SECONDARY}<seconds>",
                null,
                6
            ) {
                player.sendMessage("${CC.PRIMARY}Match started!")
                matchState = MatchState.FIGHTING
                PlayerUtil.allowMovement(player)
                started = System.currentTimeMillis()
            })
        }
    }

    open fun getMatchType(): MatchType {
        if (this is TeamMatch) {
            return MatchType.TEAM
        }

        if (this is MLGRushMatch) {
            return MatchType.BEDFIGHTS
        }

        return MatchType.NORMAL
    }

    open fun addSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.SPECTATING
        profile?.spectatingMatch = uuid

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e started spectating!")
        } else {
            sendMessage("&7[S] &a${player.name}&e started spectating!", "lpractice.silent")
        }

        players.forEach {
            if (!it.offline) {
                player.showPlayer(it.player)
            }
        }

        PlayerUtil.reset(player)
        player.gameMode = GameMode.CREATIVE

        Hotbar.giveHotbar(profile)

        val matchSpectator = MatchSpectator(player.uniqueId, player.name)
        spectators.add(matchSpectator)
    }

    open fun removeSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e stopped spectating!")
        } else {
            sendMessage("&7[S] &a${player.name}&e stopped spectating!", "lpractice.silent")
        }

        removeSpec(player)
        spectators.removeIf { it.uuid == player.uniqueId }
    }

    private fun removeSpec(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.LOBBY
        profile?.spectatingMatch = null

        players.forEach {
            if (!it.offline) {
                player.hidePlayer(it.player)
            }
        }

        if (player != null) {
            PlayerUtil.reset(player)

            Hotbar.giveHotbar(profile!!)

            if (Constants.SPAWN != null) {
                player.teleport(Constants.SPAWN)
            }
        }
    }

    open fun forceRemoveSpectator(player: Player) {
        removeSpec(player)
        spectators.removeIf { it.uuid == player.uniqueId }
    }

    open fun canHit(player: Player, target: Player): Boolean {
        return true
    }

    open fun addPlayer(player: Player, location: Location) {
        val elo = Profile.getByUUID(player.uniqueId)!!.getKitStatistic(kit.name)!!.elo

        val matchPlayer = MatchPlayer(player.uniqueId, player.name, location, elo)
        players.add(matchPlayer)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun sendMessage(message: String) {
        players.filter { !it.offline }.map { it.player }.forEach { it.sendMessage(CC.translate(message)) }
        spectators.filter { Bukkit.getPlayer(uuid) != null }.map { it.player }
            .forEach { it.sendMessage(CC.translate(message)) }
    }

    fun sendMessage(message: String, permission: String) {
        players.stream().map { it.player }
            .forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
        spectators.stream().map { it.player }
            .forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
    }

    open fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.offline) {

            sendMessage(Locale.PLAYER_DISCONNECTED.getMessage().replace("<player>", player.name))

        } else if (player.lastDamager == null && !player.offline) {

            sendMessage(Locale.PLAYER_DIED.getMessage().replace("<player>", player.name))

        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage(Locale.PLAYED_KILLED.getMessage().replace("<killer>", matchPlayer!!.name).replace("<player>", player.name))
        }

        end(mutableListOf(player))
    }

    open fun end(losers: MutableList<MatchPlayer>) {
        countdowns.forEach { it.cancel() }

        val winners = players.filter { !losers.contains(it) }
            .toMutableList()

        matchState = MatchState.ENDING

        winners.forEach { winner ->
            losers.forEach { winner.player.hidePlayer(it.player) }
        }

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val bukkitPlayer = matchPlayer.player
            val profile = Profile.getByUUID(matchPlayer.uuid)

            if (!losers.contains(matchPlayer) && !friendly) {
                StatisticManager.win(profile!!, profile, kit, ranked)
            } else {
                if (!friendly) {
                    StatisticManager.loss(profile!!, kit, ranked)
                }
            }

            if (profile!!.arrowCooldown != null) {
                profile.arrowCooldown!!.cancel()
                profile.arrowCooldown = null
            }

            if (profile.enderPearlCooldown != null) {
                profile.enderPearlCooldown!!.cancel()
                profile.enderPearlCooldown = null
            }

            val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)

            snapshot.potionsThrown = matchPlayer.potionsThrown
            snapshot.potionsMissed = matchPlayer.potionsMissed
            snapshot.longestCombo = matchPlayer.longestCombo
            snapshot.totalHits = matchPlayer.hits
            snapshot.opponent = getOpponent(bukkitPlayer.uniqueId)?.uuid

            snapshots.add(snapshot)

            PlayerUtil.reset(bukkitPlayer)
            PlayerUtil.allowMovement(bukkitPlayer)

            if (!friendly) {
                MatchManager.createReQueueItem(bukkitPlayer, this)
            }
        }

        for (snapshot in snapshots) {
            snapshot.createdAt = System.currentTimeMillis()
            MatchSnapshot.snapshots.add(snapshot)
        }

        sendTitleBar(winners)
        endMessage(winners, losers)

        for (matchPlayer in players)
        {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player

            player.sendMessage("${CC.PRIMARY}ELO Updates: ${CC.GREEN}${getCombinedNames(winners, " (+<elo>)")}${CC.GRAY}, ${CC.RED}${getCombinedNames(losers, " (-<elo>)")}")
        }

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                val bukkitPlayer = matchPlayer.player
                val profile = Profile.getByUUID(matchPlayer.uuid)

                players.stream().filter { !it.offline }.map { it.player }
                    .forEach {
                        val targetProfile = Profile.getByUUID(it.uniqueId)

                        if (rematchingPlayers.contains(it.uniqueId) && targetProfile!!.match == profile!!.match)
                            return@forEach

                        bukkitPlayer.hidePlayer(it)
                        it.hidePlayer(bukkitPlayer)
                    }

                if (!rematchingPlayers.contains(matchPlayer.uuid)) {
                    CustomItemStack.customItemStacks.removeIf { it.uuid == matchPlayer.uuid }
                }

                if ((profile!!.state == ProfileState.MATCH || profile.state == ProfileState.QUEUE) && profile.match == uuid) {
                    if (profile.state != ProfileState.QUEUE) {
                        profile.state = ProfileState.LOBBY
                    }

                    profile.match = null

                    if (Constants.SPAWN != null) {
                        bukkitPlayer.teleport(Constants.SPAWN)
                    }

                    Hotbar.giveHotbar(profile)
                }

                ratingMessage(profile)
            }

            if (spectators.isNotEmpty()) {
                for (i in 0 until spectators.size) {
                    val player = spectators[i].player ?: continue
                    removeSpec(player)
                }
            }

            spectators.clear()
            snapshots.clear()

            reset()
            matches.remove(this)
            arena.free = true
        }, 60L)
    }

    open fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        val snapshot = MatchSnapshot(matchPlayer.player, matchPlayer.dead)

        snapshot.potionsThrown = matchPlayer.potionsThrown
        snapshot.potionsMissed = matchPlayer.potionsMissed
        snapshot.longestCombo = matchPlayer.longestCombo
        snapshot.totalHits = matchPlayer.hits
        snapshot.opponent = getOpponent(matchPlayer.uuid)?.uuid

        snapshots.add(snapshot)

        handleDeath(matchPlayer)
    }

    private fun ratingMessage(profile: Profile) {
        if (!profile.settings.mapRating) return
        if (ArenaRatingManager.hasRated(profile.uuid, arena)) return
        if (profile.player == null || !profile.player.isOnline) return

        val fancyMessage = FancyMessage()
            .text("${CC.PRIMARY}Rate The Map: ")
            .then()
            .text("${CC.DARK_RED}[1] ")
            .command("/ratemap ${arena.name} 1")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.RED}[2] ")
            .command("/ratemap ${arena.name} 2")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.YELLOW}[3] ")
            .command("/ratemap ${arena.name} 3")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.GREEN}[4] ")
            .command("/ratemap ${arena.name} 4")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.DARK_GREEN}[5] ")
            .command("/ratemap ${arena.name} 5")
            .tooltip("${CC.PRIMARY}Click to vote!")

        fancyMessage.send(profile.player)
    }

    open fun endMessage(winners: MutableList<MatchPlayer>, losers: MutableList<MatchPlayer>) {
        val fancyMessage = TextBuilder()
            .setText("${CC.PRIMARY}${CC.BOLD}Match Results ${CC.RESET}${CC.GRAY}(click player to view)\n")
            .then()
            .setText("${CC.GREEN}Winner: ")
            .then()

        var wi = 1
        for (matchPlayer in winners) {
            if (wi < winners.size) {
                fancyMessage.setText("${CC.PRIMARY}${matchPlayer.name}${CC.GRAY}, ")
            } else {
                fancyMessage.setText("${CC.PRIMARY}${matchPlayer.name}")
            }

            fancyMessage.setCommand("/matchsnapshot ${matchPlayer.uuid}")
            fancyMessage.then()
            wi++
        }

        fancyMessage.setText("${CC.GRAY} ⎟ ${CC.RED}Loser: ").then()

        var i = 1
        for (matchPlayer in losers) {

            if (i < losers.size) {
                fancyMessage.setText("${CC.PRIMARY}${matchPlayer.name}${CC.GRAY}, ")
            } else {
                fancyMessage.setText("${CC.PRIMARY}${matchPlayer.name}")
            }

            fancyMessage.setCommand("/matchsnapshot ${matchPlayer.uuid}")
            fancyMessage.then()
            i++
        }

        if (spectators.isNotEmpty()) {
            fancyMessage.setText("\n\n${CC.PRIMARY}Spectators ${CC.GRAY}(${spectators.size})${CC.GREEN}: ")
                .then()
                .setText(Joiner.on("${CC.GRAY}, ${CC.RESET}").join(spectators.map { it.name }))
                .then()
        }

        val message = fancyMessage.build()

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player

            player.sendMessage(" ")
            player.spigot().sendMessage(message)
            player.sendMessage(" ")
        }

        for (spectator in spectators) {
            val player = spectator.player ?: continue

            player.sendMessage(" ")
            player.spigot().sendMessage(message)
            player.sendMessage(" ")
        }
    }

    private fun sendTitleBar(winners: MutableList<MatchPlayer>) {
        players.forEach {
            if (it.offline) return@forEach
            TitleBar.sendTitleBar(it.player, "${CC.SECONDARY}${getCombinedNames(winners)}${CC.PRIMARY} won!", null, 10, 60, 10)
        }
    }

    fun getCombinedNames(players: MutableList<MatchPlayer>, suffix: String = ""): String {
        return Joiner.on(", ").join(players.map { it.name + suffix.replace("<elo>", getEloUpdate(it).toString()) }.toList())
    }

    private fun getEloUpdate(matchPlayer: MatchPlayer): Int {
        return Profile.getByUUID(matchPlayer.uuid)!!.getKitStatistic(kit.name)!!.elo - matchPlayer.initialElo
    }

    fun getTime(): String {
        if (matchState == MatchState.STARTING) {
            return "${CC.GREEN}Starting"
        }

        if (matchState == MatchState.ENDING) {
            return "${CC.RED}Ending"
        }

        return TimeUtil.millisToTimer(System.currentTimeMillis() - started)
    }

    open fun reset() {
        blocksPlaced.forEach { it.type = Material.AIR }
        droppedItems.forEach { it.remove() }
    }

    fun getMatchPlayer(uuid: UUID): MatchPlayer? {
        return players.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun getOpponent(uuid: UUID): MatchPlayer? {
        return players.stream().filter { it.uuid != uuid }
            .findFirst().orElse(null)
    }

    open fun getOpponentString(uuid: UUID): String? {
        return getOpponent(uuid)?.name
    }

    fun getAlivePlayers(): MutableList<MatchPlayer> {
        return players.stream().filter { !it.dead && !it.offline }
            .collect(Collectors.toList())
    }

    companion object {
        @JvmStatic
        val matches: MutableList<Match?> = mutableListOf()

        @JvmStatic
        fun getByUUID(uuid: UUID): Match? {
            return matches.stream().filter { match: Match? -> match?.uuid == uuid }
                .findFirst().orElse(null)
        }

        @JvmStatic
        fun inMatch(): Int {
            var count = 0

            for (match in matches) {
                count += match!!.players.size
            }

            return count
        }
    }
}