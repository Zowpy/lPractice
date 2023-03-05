package net.lyragames.practice.match.impl

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cooldown
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.team.Team
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.EloUtil
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 3/29/2022
 * Project: lPractice
 */

class MLGRushMatch(kit: Kit, arena: Arena, ranked: Boolean) : TeamMatch(kit, arena, ranked) {

    private var round = 1

    override fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            PlayerUtil.reset(player)
            PlayerUtil.denyMovement(player)

            player.teleport(matchPlayer.spawn)

            CustomItemStack.getCustomItemStacks().removeIf { it.uuid == matchPlayer.uuid }

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
                PlayerUtil.allowMovement(player)
            })
        }
    }

    fun handleBreak(event: BlockBreakEvent) {

        if (matchState != MatchState.FIGHTING) {
            event.isCancelled = true
            return
        }

        val player = getMatchPlayer(event.player.uniqueId)

        if (player?.dead!! || player.respawning) {
            event.isCancelled = true
            return
        }

        if (blocksPlaced.contains(event.block)) {
            blocksPlaced.remove(event.block)
        } else {
            event.isCancelled = true
        }

        if (event.block.type == Material.BED || event.block.type == Material.BED_BLOCK) {
            for (matchPlayer in players) {
                for (x in event.block.x - 2 until event.block.x + 2) {
                    for (y in event.block.y - 2 until event.block.y + 2) {
                        for (z in event.block.z - 2 until event.block.z + 2) {
                            if (matchPlayer.bed?.blockX == x && matchPlayer.bed?.blockY == y && matchPlayer.bed?.blockZ == z) {

                                if (player?.bed?.blockX == x && player.bed?.blockY == y && player.bed?.blockZ == z) {
                                    player.player.sendMessage("${CC.RED}You cannot break your own bed.")
                                    event.isCancelled = true
                                    break
                                }

                                event.isCancelled = true
                                player!!.points++
                                resetMatch(player as TeamMatchPlayer)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleDeath(player: MatchPlayer) {
        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has died from natural causes!")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        player.respawning = true

        PlayerUtil.reset(player.player)

        player.player.allowFlight = true
        player.player.isFlying = true

        players.stream().forEach { if (it.player != null) it.player.hidePlayer(player.player) }

        player.player.teleport(player.spawn.clone().add(0.0, 5.0, 0.0))

        player.respawnCountdown = Countdown(PracticePlugin.instance, player.player, "${CC.PRIMARY}Respawning in ${CC.SECONDARY}<seconds>${CC.PRIMARY}!", 6) {
            val profile = Profile.getByUUID(player.uuid)
            player.player.teleport(player.spawn)

            PlayerUtil.reset(player.player)

            profile?.getKitStatistic(kit.name)?.generateBooks(player.player)

            player.respawning = false

            players.stream().forEach { if (it.player != null) it.player.showPlayer(player.player) }

            player.respawnCountdown = null
        }
    }

    private fun resetMatch(winner: TeamMatchPlayer) {
        if (winner.points == 5) {
            end(winner)
        }else {
            round++

            countdowns.forEach { it.cancel() }

            reset()
            matchState = MatchState.STARTING

            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                if (matchPlayer.respawnCountdown != null) {
                    matchPlayer.respawnCountdown?.cancel()
                    matchPlayer.respawnCountdown?.consumer?.accept(true)
                    matchPlayer.respawnCountdown = null
                }

                val player = matchPlayer.player
                val profile = Profile.getByUUID(player.uniqueId)

                PlayerUtil.reset(player)
                PlayerUtil.denyMovement(player)

                player.teleport(matchPlayer.spawn)
                profile?.getKitStatistic(kit.name)?.generateBooks(player)

                countdowns.add(Countdown(
                    PracticePlugin.instance,
                    player,
                    "${CC.PRIMARY}Round ${CC.SECONDARY}$round ${CC.PRIMARY}starting in ${CC.SECONDARY}<seconds>${CC.PRIMARY} seconds!",
                    6
                ) {
                    player.sendMessage("${CC.PRIMARY}Round started!")
                    matchState = MatchState.FIGHTING
                    PlayerUtil.allowMovement(player)
                })
            }

        }
    }

    private fun end(player: TeamMatchPlayer) {
        reset()

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

                CustomItemStack.getCustomItemStacks().removeIf { it.uuid == matchPlayer.uuid }

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
                endMessage(player, it)
                sendTitle("&a${it.name}&e's VICTORY!", "&eThe duel has ended!")

                val profile = Profile.getByUUID(it.uuid)

                ratingMessage(profile!!)
            }

            matches.remove(this)
            reset()
            arena.free = true
        }, 20L)
    }

    override fun endMessage(winner: MatchPlayer, loser: MatchPlayer) {
       // val losingTeam = teams.stream().filter { team -> !team.players.stream().map }.findAny().orElse(null)
      //  val winningTeam = teams.stream().filter { it.uuid != losingTeam.uuid }.findAny().orElse(null)

        val losingTeam = getTeam((loser as TeamMatchPlayer).teamUniqueId)
        val winningTeam = getOpponentTeam(losingTeam!!)

        /*for (team in teams) {

            var points = 0

            for (matchPlayer in team.players) {
                points += matchPlayer.points
            }

            if (points == 5) {
                winningTeam = team
            }else {
                losingTeam = team
            }
        }

        if (winningTeam == null) {

        } */


        val fancyMessage = FancyMessage()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .text("${CC.GREEN}Winner: ")
            .then()

        var wi = 1
        for (matchPlayer in winningTeam?.players!!) {
            if (wi < winningTeam.players.size) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}\n")
            }

            fancyMessage.command("/matchsnapshot ${matchPlayer.uuid.toString()}")
            fancyMessage.then()
            wi++
        }

        fancyMessage.text("${CC.RED}Loser: ").then()

        var i = 1
        for (matchPlayer in losingTeam?.players!!) {

            if (i < losingTeam.players.size) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}\n")
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
            forceRemoveSpectator(spectator.player)
        }
    }

    override fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

       // handleDeath(matchPlayer)

        if (teams.stream().anyMatch { team -> team.players.stream().noneMatch { !it.offline } }) {
            end(players.stream().filter { !it.offline }.findAny().orElse(null) as TeamMatchPlayer)
        }
    }
}