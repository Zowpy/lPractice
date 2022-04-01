package net.lyragames.practice.match

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.title.TitleBar
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.spectator.MatchSpectator
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.EloUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
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
    val countdowns: MutableList<Countdown> = mutableListOf()

    open fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            PlayerUtil.reset(player)
            PlayerUtil.allowMovement(player)

            player.teleport(matchPlayer.spawn)
            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            countdowns.add(Countdown(
                PracticePlugin.instance,
                player,
                "&aMatch starting in <seconds> seconds!",
                6
            ) {
                player.sendMessage(CC.GREEN + "Match started!")
                matchState = MatchState.FIGHTING
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
        }else {
            sendMessage("&7[S] &a${player.name}&e started spectating!", "lpractice.silent")
        }

        players.forEach {
            player.showPlayer(it.player)
        }

        PlayerUtil.reset(player)
        player.gameMode = GameMode.CREATIVE

        Hotbar.giveHotbar(profile)

        val matchSpectator = MatchSpectator(player.uniqueId, player.name)
        spectators.add(matchSpectator)
    }

    open fun removeSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.LOBBY
        profile?.spectatingMatch = null

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e stopped spectating!")
        }else {
            sendMessage("&7[S] &a${player.name}&e stopped spectating!", "lpractice.silent")
        }

        players.forEach {
            player.hidePlayer(it.player)
        }

        PlayerUtil.reset(player)

        spectators.removeIf { it.uuid == player.uniqueId }

        Hotbar.giveHotbar(profile)

        if (Constants.SPAWN != null) {
            player.teleport(Constants.SPAWN)
        }
    }

    open fun forceRemoveSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.LOBBY
        profile?.spectatingMatch = null

        players.forEach {
            player.hidePlayer(it.player)
        }

        PlayerUtil.reset(player)

        spectators.removeIf { it.uuid == player.uniqueId }

        Hotbar.giveHotbar(profile!!)

        if (Constants.SPAWN != null) {
            player.teleport(Constants.SPAWN)
        }
    }

    open fun canHit(player: Player, target: Player): Boolean {
        return true
    }

    open fun addPlayer(player: Player, location: Location) {
        val matchPlayer = MatchPlayer(player.uniqueId, player.name, location)
        players.add(matchPlayer)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun sendMessage(message: String) {
        players.stream().map { it.player }.forEach { it.sendMessage(CC.translate(message)) }
        spectators.stream().map { it.player }.forEach { it.sendMessage(CC.translate(message)) }
    }

    fun sendMessage(message: String, permission: String) {
        players.stream().map { it.player }.forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
        spectators.stream().map { it.player }.forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
    }

    open fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has died from natural causes!")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        countdowns.forEach { it.cancel() }

        matchState = MatchState.ENDING
        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            var loserProfile: Profile? = Profile.getByUUID(player.uuid)

            if (loserProfile == null) {
                val newProfile = Profile(player.uuid, player.name)
                newProfile.load()

                loserProfile = newProfile
            }

            for (matchPlayer in players) {
                if (matchPlayer.offline) continue
                val winner = matchPlayer.uuid != player.uuid

                val bukkitPlayer = matchPlayer.player
                val profile = Profile.getByUUID(matchPlayer.uuid)

                val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)
                snapshot.potionsThrown = matchPlayer.potionsThrown
                snapshot.potionsMissed = matchPlayer.potionsMissed
                snapshot.longestCombo = matchPlayer.longestCombo
                snapshot.totalHits = matchPlayer.hits
                snapshot.opponent = getOpponent(bukkitPlayer.uniqueId)?.uuid

                snapshots.add(snapshot)

                PlayerUtil.reset(bukkitPlayer)
                PlayerUtil.allowMovement(bukkitPlayer)
                profile?.match = null
                profile?.state = ProfileState.LOBBY

                if (Constants.SPAWN != null) {
                    bukkitPlayer.teleport(Constants.SPAWN)
                }

                Hotbar.giveHotbar(profile!!)

                players.stream().filter { !it.offline }.map { it.player }
                    .forEach {
                        if (it.player == null) return@forEach
                        bukkitPlayer.hidePlayer(it)
                        it.hidePlayer(bukkitPlayer)
                    }

                if (winner && !friendly) {
                    val globalStatistics = profile.globalStatistic

                    globalStatistics.wins++
                    globalStatistics.streak++

                    if (globalStatistics.streak >= globalStatistics.bestStreak) {
                        globalStatistics.bestStreak = globalStatistics.streak
                    }

                    val kitStatistic = profile.getKitStatistic(kit.name)!!

                    kitStatistic.wins++

                    if (ranked) {
                        kitStatistic.rankedWins++
                        val elo = loserProfile.getKitStatistic(kit.name)?.elo
                        loserProfile.getKitStatistic(kit.name)?.elo = loserProfile.getKitStatistic(kit.name)?.elo?.plus(elo?.let { EloUtil.getNewRating(it, kitStatistic.elo, false) }!!)!!
                        kitStatistic.elo =+ EloUtil.getNewRating(kitStatistic.elo, loserProfile.getKitStatistic(kit.name)?.elo!!, true)

                        if (kitStatistic.elo >= kitStatistic.peakELO) {
                            kitStatistic.peakELO = kitStatistic.elo
                        }
                    }

                    kitStatistic.currentStreak++

                    if (kitStatistic.currentStreak >= kitStatistic.bestStreak) {
                        kitStatistic.bestStreak = kitStatistic.currentStreak
                    }

                    profile.save()
                }else {
                    if (!friendly) {
                        val globalStatistics = profile.globalStatistic

                        globalStatistics.losses++
                        globalStatistics.streak = 0

                        val kitStatistic = profile.getKitStatistic(kit.name)!!

                        kitStatistic.currentStreak = 0

                        if (ranked) {
                            kitStatistic.rankedWins++
                        }

                        profile.save()
                    }
                }
            }

            for (snapshot in snapshots) {
                snapshot.createdAt = System.currentTimeMillis()
                MatchSnapshot.snapshots.add(snapshot)
            }

            getOpponent(player.uuid)?.let {
                endMessage(it, player)
                sendTitleBar(it)
            }

            matches.remove(this)
            reset()
            arena.free = true
        }, 20L)
    }

    open fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        handleDeath(matchPlayer)
    }

    open fun endMessage(winner: MatchPlayer, loser: MatchPlayer) {
        val fancyMessage = FancyMessage()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .text("${CC.GREEN}Winner: ")
            .then()
            .text("${winner.name} \n")
            .command("/matchsnapshot ${winner.uuid}")
            .then()
            .text("${CC.RED}Loser: ")
            .then()
            .text("${loser.name} \n")
            .command("/matchsnapshot ${loser.uuid}")
            .then()

        if (spectators.isNotEmpty()) {
            fancyMessage.text("\n${CC.GREEN}Spectators ${CC.GRAY}(${spectators.size})${CC.GREEN}: ")
                .then().text("${Joiner.on("${CC.GRAY}, ${CC.RESET}").join(spectators.map { it.name })}\n")
                .then().text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }else {
            fancyMessage.text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }

        for (player in players) {
            if (player.offline) continue

            fancyMessage.send(player.player)
        }

        for (spectator in spectators) {
            if (spectator.player == null) continue

            fancyMessage.send(spectator.player)
            forceRemoveSpectator(spectator.player)
        }
    }

    fun sendTitleBar(winner: MatchPlayer) {
        val titleBar = TitleBar("${CC.GREEN}${winner.name}${CC.YELLOW} won!", false)

        players.forEach { if (!it.offline) titleBar.sendPacket(it.player) }
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

    fun reset() {
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