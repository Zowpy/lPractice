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
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.team.Team
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
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

class TeamMatch(kit: Kit, arena: Arena, ranked: Boolean) : Match(kit, arena, ranked) {

    val teams: MutableList<Team> = mutableListOf()

    init {
        val team1 = Team()
        team1.spawn = arena.l1

        val team2 = Team()
        team2.spawn = arena.l2

        teams.add(team1)
        teams.add(team2)
    }

    override fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.lastDamager == null) {
            sendMessage("&c" + player.name + " &ehas died from natural causes!")
        }else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c" + player.name + " &ehas been killed by &c" + matchPlayer?.name + "&e!")
        }

        val losingTeam = teams.stream().filter { team -> !team.players.stream().anyMatch { !it.dead } }.findAny().orElse(null)

        if (losingTeam != null) {
            //ending
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
            }

            for (snapshot in snapshots) {
                snapshot.createdAt = System.currentTimeMillis()
                MatchSnapshot.snapshots.add(snapshot)
            }

            endMessage(player, player)


            matches.remove(this)
            reset()
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

           // .then()
            //.text("${CC.RED}Loser: ")
            //.then()

        var wi = 1
        for (matchPlayer in winningTeam.players) {
            if (wi < winningTeam.players.size) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}.\n")
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
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}.\n")
            }

            fancyMessage.command("/matchsnapshot ${matchPlayer.uuid.toString()}")
            fancyMessage.then()
            i++
        }

        fancyMessage.text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")

        players.stream().filter { !it.offline }
            .forEach { fancyMessage.send(it.player) }
    }

    override fun canHit(player: Player, target: Player): Boolean {
        val teamMatchPlayer = getMatchPlayer(player.uniqueId) as TeamMatchPlayer
        val teamMatchPlayer1 = getMatchPlayer(target.uniqueId) as TeamMatchPlayer

        return teamMatchPlayer.teamUniqueId != teamMatchPlayer1.teamUniqueId
    }

    private fun findTeam(): Team? {
        //return teams[ThreadLocalRandom.current().nextInt(teams.size)]
        return teams.stream().sorted { o1, o2 -> o1.players.size - o2.players.size }
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

    override fun getOpponentString(uuid: UUID): String? {
        val team = teams.stream().filter { team -> team.players.stream().anyMatch { it.uuid == uuid } }.findFirst().orElse(null)
        val opponentTeam = teams.stream().filter { it.uuid != team.uuid }.findFirst().orElse(null)

        return Joiner.on(", ").join(opponentTeam.players.stream().map { it.name }.collect(Collectors.toList()))
    }
}