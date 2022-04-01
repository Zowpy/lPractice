package net.lyragames.practice.adapter

import io.github.thatkawaiisam.assemble.AssembleAdapter
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchType
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class ScoreboardAdapter(private val configFile: ConfigFile): AssembleAdapter {

    override fun getTitle(p0: Player?): String {
        return CC.translate(configFile.getString("scoreboard.title"))
    }

    override fun getLines(player: Player): MutableList<String>? {
        val profile = Profile.getByUUID(player.uniqueId)

        if (!profile?.settings?.scoreboard!!) {
            return mutableListOf()
        }

        if (profile.state == ProfileState.LOBBY) {

            return configFile.getStringList("scoreboard.lobby").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", QueueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString()) }.collect(Collectors.toList())
        }

        if (profile.state == ProfileState.MATCH) {

            val match = profile.match?.let { Match.getByUUID(it) } ?: return mutableListOf()

            if (match is MLGRushMatch) {

                val matchPlayer = match.getMatchPlayer(player.uniqueId) as TeamMatchPlayer

                val team = match.getTeam(matchPlayer.teamUniqueId)
                val opponentTeam = match.getOpponentTeam(team!!)

                var points = 0
                var opponentPoints = 0

                for (teamMatchPlayer in team.players) {
                    points += teamMatchPlayer.points
                }

                for (teamMatchPlayer in opponentTeam?.players!!) {
                    opponentPoints += teamMatchPlayer.points
                }

                val symbol = "⬤"

                return configFile.getStringList("scoreboard.bedfight").stream()
                    .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                        .replace("<queuing>", QueueManager.inQueue().toString()))
                        .replace("<in_match>", Match.inMatch().toString())
                        .replace("<opponent>", match.getOpponentString(player.uniqueId)!!)
                        .replace("<kit>", match.kit.name)
                        .replace("<time>", match.getTime())
                        .replace("<points>", "${StringUtils.repeat("${CC.GREEN}$symbol", points)}${StringUtils.repeat("${CC.GRAY}$symbol", 5 - points)}")
                        .replace("<opponent_points>", "${StringUtils.repeat("${CC.GREEN}$symbol", opponentPoints)}${StringUtils.repeat("${CC.GRAY}$symbol", 5 - opponentPoints)}")}.collect(Collectors.toList())
            }

            if (match is TeamMatch) {
                return configFile.getStringList("scoreboard.match").stream()
                    .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                        .replace("<queuing>", QueueManager.inQueue().toString()))
                        .replace("<in_match>", Match.inMatch().toString())
                        .replace("<opponent>", match.getOpponentString(player.uniqueId)!!)
                        .replace("<kit>", match.kit.name)
                        .replace("<time>", match.getTime()) }.collect(Collectors.toList())
            }

            if (match.kit.kitData.boxing) {

                val matchPlayer = match.getMatchPlayer(player.uniqueId)

                return configFile.getStringList("scoreboard.boxing").stream()
                    .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                        .replace("<queuing>", QueueManager.inQueue().toString()))
                        .replace("<in_match>", Match.inMatch().toString())
                        .replace("<opponent>", match.getOpponentString(player.uniqueId)!!)
                        .replace("<kit>", match.kit.name)
                        .replace("<hits>", matchPlayer?.hits.toString())
                        .replace("<opponent-hits>", match.getOpponent(player.uniqueId)?.hits.toString())
                        .replace("<combo>", if (matchPlayer?.combo == 0 && matchPlayer.comboed == 0) "" else if (matchPlayer?.combo == 0) "${CC.RED}(-${matchPlayer.comboed})" else "${CC.GREEN}(+${matchPlayer?.combo})")
                        .replace("<time>", match.getTime()) }.collect(Collectors.toList())
            }

            return configFile.getStringList("scoreboard.match").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", QueueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString())
                    .replace("<opponent>", match.getOpponentString(player.uniqueId)!!)
                    .replace("<kit>", match.kit.name)
                    .replace("<time>", match.getTime()) }.collect(Collectors.toList())

        }

        if (profile.state == ProfileState.QUEUE) {

            val queuePlayer = profile.queuePlayer ?: return mutableListOf()

            return configFile.getStringList("scoreboard.queue").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", QueueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString())
                    .replace("<time>", TimeUtil.millisToTimer(System.currentTimeMillis() - queuePlayer.started))}.collect(Collectors.toList())
        }

        if (profile.state == ProfileState.FFA) {

            if (profile.ffa == null) return configFile.getStringList("scoreboard.lobby").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", QueueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString()) }.collect(Collectors.toList())

            val ffa = FFAManager.getByUUID(profile.ffa!!)
            val ffaPlayer = ffa?.getFFAPlayer(player.uniqueId)

            return configFile.getStringList("scoreboard.ffa").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<kit>", ffa?.kit?.name!!).replace("<kills>", ffaPlayer?.kills.toString())
                    .replace("<killstreak>", ffaPlayer?.killStreak.toString())
                    .replace("<deaths>", ffaPlayer?.death.toString()).replace("<ping>", PlayerUtil.getPing(player).toString())) }.collect(Collectors.toList())
        }

        if (profile.state == ProfileState.EVENT) {

            val event = EventManager.event
                ?: return configFile.getStringList("scoreboard.lobby").stream()
                    .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                        .replace("<queuing>", QueueManager.inQueue().toString()))
                        .replace("<in_match>", Match.inMatch().toString()) }.collect(Collectors.toList())



            return configFile.getStringList("scoreboard.event").stream()
                .map { CC.translate(it.replace("<state>", event.state.stateName)
                    .replace("<type>", event.type.eventName)
                    .replace("<playing1>", if (event.playingPlayers.isEmpty()) "N/A" else event.playingPlayers[0].player.name)
                    .replace("<playing2>", if (event.playingPlayers.isEmpty()) "N/A" else event.playingPlayers[1].player.name)) }
                .collect(Collectors.toList())
        }

        if (profile.state == ProfileState.SPECTATING) {

            val match = Match.getByUUID(profile.spectatingMatch!!) ?: return configFile.getStringList("scoreboard.lobby").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", QueueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString()) }.collect(Collectors.toList())

            if (match.getMatchType() == MatchType.TEAM) {
                val randPlayer = (match as TeamMatch).players[0]

                return configFile.getStringList("scoreboard.spectate").stream()
                    .map { CC.translate(it.replace("<kit>", match.kit.name)
                        .replace("<time>", match.getTime())
                        .replace("<team1>", match.getPlayerString(randPlayer.uuid)!!)
                        .replace("<team2>", match.getOpponentString(randPlayer.uuid)!!)) }.collect(Collectors.toList())
            }

            return configFile.getStringList("scoreboard.spectate").stream()
                .map { CC.translate(it.replace("<kit>", match.kit.name)
                    .replace("<time>", match.getTime())
                    .replace("<team1>", match.players[0].name)
                    .replace("<team2>", match.players[1].name)) }.collect(Collectors.toList())
        }

        return mutableListOf()
    }
}