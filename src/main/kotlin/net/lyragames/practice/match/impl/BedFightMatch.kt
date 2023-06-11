package net.lyragames.practice.match.impl

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.title.TitleBar
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.StatisticManager
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
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import kotlin.streams.toList


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/31/2022
 * Project: lPractice
 */

class BedFightMatch(kit: Kit, arena: Arena, ranked: Boolean) : TeamMatch(kit, arena, ranked) {

    init {
        teams.clear()

        val team1 = Team("Red")
        team1.spawn = (arena as StandaloneBedWarsArena).redSpawn
        team1.bedLocation = arena.redBed

        val team2 = Team("Blue")
        team2.spawn = arena.blueSpawn
        team2.bedLocation = arena.blueBed

        teams.add(team1)
        teams.add(team2)
    }

    override fun addPlayer(player: Player, location: Location) {
        val team = findTeam()
        val teamMatchPlayer = TeamMatchPlayer(player.uniqueId, player.name, team?.spawn!!, team.uuid!!)

        team.players.add(teamMatchPlayer)

        players.stream().map { it.player }.forEach {
            it.showPlayer(player)
            player.showPlayer(it)
        }
        players.add(teamMatchPlayer)
    }

    override fun start() {
        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            PlayerUtil.reset(player)
            PlayerUtil.denyMovement(player)

            val team = getTeam((matchPlayer as TeamMatchPlayer).teamUniqueId)

            matchPlayer.spawn = team?.spawn!!
            matchPlayer.bed = team.bedLocation

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
                                val team = getTeam((matchPlayer as TeamMatchPlayer).teamUniqueId)

                                val titleBar = TitleBar("${CC.RED}Your bed has been destroyed", false)

                                team?.players?.forEach { if (it.player != null) titleBar.sendPacket(it.player) }

                                sendMessage("${CC.SECONDARY}${player.name}${CC.PRIMARY} has broke ${CC.SECONDARY}${team?.name}${CC.PRIMARY}'s bed!")

                                team?.broken = true
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleDeath(player: MatchPlayer) {

        val team = getTeam((player as TeamMatchPlayer).teamUniqueId)

        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has died from natural causes!${if (team?.broken!!) "${CC.GRAY} (${CC.PRIMARY}FINAL${CC.GRAY})" else ""}")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!${if (team?.broken!!) "${CC.GRAY} (${CC.PRIMARY}FINAL${CC.GRAY})" else ""}")
        }

        if (player.offline && team?.players?.none { !it.dead && !it.offline }!!) {
            end(team.players.map { getMatchPlayer(it.uuid)!! }.toMutableList())
            return
        }

        player.respawning = true

        if (team?.broken!!) {
            PlayerUtil.reset(player.player)

            player.player.allowFlight = true
            player.player.isFlying = true

            players.stream().forEach { if (it.player != null) it.player.hidePlayer(player.player) }

            player.player.teleport(player.spawn.clone().add(0.0, 5.0, 0.0))


            if (team.players.none { !it.dead && !it.offline }) {
                end(team.players.map { getMatchPlayer(it.uuid)!! }.toMutableList())
            }
            return
        }

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

    /*private fun end(player: TeamMatchPlayer) {
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

                if (matchPlayer.respawnCountdown != null) {
                    matchPlayer.respawnCountdown?.cancel()
                    matchPlayer.respawnCountdown = null
                }

                players.stream().filter { !it.offline }.map { it.player }
                    .forEach {
                        if (it.player == null) return@forEach
                        bukkitPlayer.hidePlayer(it)
                        it.hidePlayer(bukkitPlayer)
                    }

                if (winner && !friendly) {
                    StatisticManager.win(profile, loserProfile, kit, ranked)
                }else {
                    if (!friendly) {
                        StatisticManager.loss(profile, kit, ranked)
                    }
                }
            }

            for (snapshot in snapshots) {
                snapshot.createdAt = System.currentTimeMillis()
                MatchSnapshot.snapshots.add(snapshot)
            }

            getOpponent(player.uuid)?.let {
                endMessage(player, it)
                sendTitleBar(player)

                val profile = Profile.getByUUID(it.uuid)

                ratingMessage(profile!!)
                ratingMessage(Profile.getByUUID(player.uuid)!!)
            }

            matches.remove(this)
            reset()
            arena.free = true
        }, 20L)
    } */

    /*override fun endMessage(winner: MatchPlayer, loser: MatchPlayer) {
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
    } */

    override fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        if (teams.stream().anyMatch { team -> team.players.stream().noneMatch { !it.offline } }) {
            val team = teams.stream().filter { team -> team.players.stream().noneMatch { !it.dead && !it.offline } }.findFirst().orElse(null)
            team!!.players.map { getMatchPlayer(it.uuid)!! }.toMutableList().let { end(it) }
        }
    }
}