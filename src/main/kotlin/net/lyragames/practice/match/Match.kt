package net.lyragames.practice.match

import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
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

    val uuid: UUID = UUID.randomUUID()
    val party = false
    var matchState = MatchState.STARTING
    val started = System.currentTimeMillis()
    val players: MutableList<MatchPlayer> = mutableListOf()
    val blocksPlaced: MutableList<Block> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()
    val snapshots: MutableList<MatchSnapshot> = mutableListOf()

    fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            PlayerUtil.reset(player)

            player.teleport(matchPlayer.spawn)
            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            Countdown(
                PracticePlugin.instance,
                player,
                "&aMatch starting in <seconds> seconds!",
                6
            ) {
                player.sendMessage(CC.GREEN + "Match started!")
                matchState = MatchState.FIGHTING
            }
        }
    }

    open fun getMatchType(): MatchType {
        return if (this is TeamMatch) {
            MatchType.TEAM
        }else {
            MatchType.NORMAL
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
        players.stream().map { matchPlayer -> matchPlayer.player }.forEach{ player -> player.sendMessage(CC.translate(message)) }
    }

    open fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.lastDamager == null) {
            sendMessage("&c" + player.name + " &ehas died from natural causes!")
        }else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c" + player.name + " &ehas been killed by &c" + matchPlayer?.name + "&e!")
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
            profile?.match = null
            profile?.state = ProfileState.LOBBY

            if (Constants.SPAWN != null) {
                bukkitPlayer.teleport(Constants.SPAWN)
            }

            Hotbar.giveHotbar(profile!!)

            players.stream().map { it.player }
                .forEach {
                    bukkitPlayer.hidePlayer(it)
                    it.hidePlayer(bukkitPlayer)
                }

            if (winner) {
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
                    kitStatistic.elo =+ 13

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
                val globalStatistics = profile.globalStatistic

                globalStatistics.losses++
                globalStatistics.streak = 0

                val kitStatistic = profile.getKitStatistic(kit.name)!!

                kitStatistic.currentStreak = 0

                if (ranked) {
                    kitStatistic.rankedWins++
                    kitStatistic.elo =- 13
                }

                profile.save()
            }
        }

        for (snapshot in snapshots) {
            snapshot.createdAt = System.currentTimeMillis()
            MatchSnapshot.snapshots.add(snapshot)
        }

        getOpponent(player.uuid)?.let { endMessage(it, player) }

        matches.remove(this)
        reset()
    }

    open fun endMessage(winner: MatchPlayer, loser: MatchPlayer) {
        val fancyMessage = FancyMessage()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .text("${CC.GREEN}Winner: ")
            .then()
            .text("${winner.name} \n")
            .command("/matchsnapshot ${winner.uuid.toString()}")
            .then()
            .text("${CC.RED}Loser: ")
            .then()
            .text("${loser.name} \n")
            .command("/matchsnapshot ${loser.uuid.toString()}")
            .then()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")

        players.stream().filter { !it.offline }
            .forEach { fancyMessage.send(it.player) }
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