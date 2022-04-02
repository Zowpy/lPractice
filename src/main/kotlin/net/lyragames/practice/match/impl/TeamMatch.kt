package net.lyragames.practice.match.impl

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.team.Team
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/12/2022
 * Project: lPractice
 */

open class TeamMatch(kit: Kit, arena: Arena, ranked: Boolean) : Match(kit, arena, ranked) {

    val teams: MutableList<Team> = mutableListOf()

    init {
        val team1 = Team("Red")
        team1.spawn = arena.l1

        val team2 = Team("Blue")
        team2.spawn = arena.l2

        teams.add(team1)
        teams.add(team2)
    }

    override fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.lastDamager == null) {
            sendMessage("&c" + player.name + " ${CC.PRIMARY}has died from natural causes!")
        }else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c" + player.name + " ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        val losingTeam = teams.stream().filter { team -> !team.players.stream().anyMatch { !it.dead } }.findAny().orElse(null)

        if (losingTeam != null) {
            //ending
            countdowns.forEach { it.cancel() }

            matchState = MatchState.ENDING
            Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
                for (matchPlayer in players) {
                    if (matchPlayer.offline) continue

                    val bukkitPlayer = matchPlayer.player
                    val profile = Profile.getByUUID(matchPlayer.uuid)

                    val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)
                    snapshot.potionsThrown = matchPlayer.potionsThrown
                    snapshot.potionsMissed = matchPlayer.potionsMissed
                    snapshot.longestCombo = matchPlayer.longestCombo
                    snapshot.totalHits = matchPlayer.hits

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
                }

                for (snapshot in snapshots) {
                    snapshot.createdAt = System.currentTimeMillis()
                    MatchSnapshot.snapshots.add(snapshot)
                }

                endMessage(player, player)


                matches.remove(this)
                reset()
            }, 20L)
        }else {
            //not ending
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
        val losingTeam = teams.stream().filter { team -> !team.players.stream().anyMatch { !it.dead } }.findAny().orElse(null)
        val winningTeam = teams.stream().filter { it.uuid != losingTeam.uuid }.findAny().orElse(null)

        val fancyMessage = FancyMessage()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .text("${CC.GREEN}Winner: ")
            .then()

        var wi = 1
        for (matchPlayer in winningTeam.players) {
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
        for (matchPlayer in losingTeam.players) {

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

    override fun canHit(player: Player, target: Player): Boolean {
        val teamMatchPlayer = getMatchPlayer(player.uniqueId) as TeamMatchPlayer
        val teamMatchPlayer1 = getMatchPlayer(target.uniqueId) as TeamMatchPlayer

        return teamMatchPlayer.teamUniqueId != teamMatchPlayer1.teamUniqueId
    }

    fun findTeam(): Team? {
        //return teams[ThreadLocalRandom.current().nextInt(teams.size)]
        return teams.stream().sorted { o1, o2 -> o1.players.size - o2.players.size }
            .findFirst().orElse(null)
    }

    fun getTeam(uuid: UUID): Team? {
        return teams.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun getOpponentTeam(team: Team): Team? {
        return teams.stream().filter { it.uuid != team.uuid }
            .findFirst().orElse(null)
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

    fun addPlayer(player: Player, team: Team) {
        val teamMatchPlayer = TeamMatchPlayer(player.uniqueId, player.name, team.spawn!!, team.uuid!!)

        team.players.add(teamMatchPlayer)

        players.stream().map { it.player }.forEach {
            it.showPlayer(player)
            player.showPlayer(it)
        }
        players.add(teamMatchPlayer)
    }

    override fun getOpponentString(uuid: UUID): String? {
        val team = getTeam((getMatchPlayer(uuid) as TeamMatchPlayer).teamUniqueId)
        val opponentTeam = getOpponentTeam(team!!)

        return Joiner.on(", ").join(opponentTeam!!.players.stream().map { it.name }.collect(Collectors.toList()))
    }

    fun getPlayerString(uuid: UUID): String? {
        val team = getTeam((getMatchPlayer(uuid) as TeamMatchPlayer).teamUniqueId)

        return Joiner.on(", ").join(team!!.players.stream().map { it.name }.collect(Collectors.toList()))
    }
}