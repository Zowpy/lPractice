package net.lyragames.practice.match.impl

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class PartyFFAMatch(kit: Kit, arena: Arena) : Match(kit, arena, false) {

    override fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.lastDamager == null) {
            sendMessage("&c" + player.name + " &ehas died from natural causes!")
        }else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c" + player.name + " &ehas been killed by &c" + matchPlayer?.name + "&e!")
        }

        var winner: MatchPlayer? = null

        if (getAlivePlayers().size <= 1) {
            for (matchPlayer in players) {
                if (matchPlayer.offline) continue
                val winnerBoolean = getAlivePlayers().stream().findFirst().orElse(null).uuid == matchPlayer.uuid

                if (winnerBoolean) {
                    winner = matchPlayer
                }

                val bukkitPlayer = matchPlayer.player
                val profile = Profile.getByUUID(matchPlayer.uuid)

                val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)
                snapshot.potionsThrown = matchPlayer.potionsThrown
                snapshot.potionsMissed = matchPlayer.potionsMissed
                snapshot.longestCombo = matchPlayer.longestCombo
                snapshot.totalHits = matchPlayer.hits

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

               /* if (winner) {
                    val globalStatistics = profile.globalStatistic

                    globalStatistics.wins++
                    globalStatistics.streak++

                    if (globalStatistics.streak >= globalStatistics.bestStreak) {
                        globalStatistics.bestStreak = globalStatistics.streak
                    }

                    val kitStatistic = profile.getKitStatistic(kit.name)!!

                    kitStatistic.wins++

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
                } */
            }

            for (snapshot in snapshots) {
                snapshot.createdAt = System.currentTimeMillis()
                MatchSnapshot.snapshots.add(snapshot)
            }

            if (winner != null) {
                endMessage(winner, player)
            }

            matches.remove(this)
            reset()
        }else {
            val bukkitPlayer = player.player

            PlayerUtil.reset(bukkitPlayer)

            val profile = Profile.getByUUID(player.uuid)
            profile?.state = ProfileState.SPECTATING

            val snapshot = MatchSnapshot(bukkitPlayer, player.dead)
            snapshot.potionsThrown = player.potionsThrown
            snapshot.potionsMissed = player.potionsMissed
            snapshot.longestCombo = player.longestCombo

            snapshots.add(snapshot)

            bukkitPlayer.allowFlight = true
            bukkitPlayer.isFlying = true

            players.stream().map { it.player }
                .forEach {
                    it.hidePlayer(bukkitPlayer)
                }
        }
    }

    override fun endMessage(winner: MatchPlayer, loser: MatchPlayer) {
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

        var i = 1
        for (matchPlayer in players) {
            if (matchPlayer.uuid == winner.uuid) continue

            if (i < players.size - 1) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}.\n")
            }

            fancyMessage.command("/matchsnapshot ${matchPlayer.uuid.toString()}")
            fancyMessage.then()
            i++
        }

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
        }
    }
}