package net.lyragames.practice.adapter

import io.github.thatkawaiisam.assemble.AssembleAdapter
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ConfigFile
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchType
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.stream.Collector
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class ScoreboardAdapter(val configFile: ConfigFile): AssembleAdapter {

    override fun getTitle(p0: Player?): String {
        return CC.translate(configFile.getString("scoreboard.title"))
    }

    override fun getLines(player: Player): MutableList<String>? {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY) {

            return configFile.getStringList("scoreboard.lobby").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", PracticePlugin.instance.queueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString()) }.collect(Collectors.toList())
        }

        if (profile?.state == ProfileState.MATCH) {

            val match = profile.match?.let { Match.getByUUID(it) }

            if (match == null) return mutableListOf()

            if (match.getMatchType() == MatchType.TEAM) {
                return configFile.getStringList("scoreboard.match").stream()
                    .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                        .replace("<queuing>", PracticePlugin.instance.queueManager.inQueue().toString()))
                        .replace("<in_match>", Match.inMatch().toString())
                        .replace("<opponent>", (match as TeamMatch).getOpponentString(player.uniqueId)!!)
                        .replace("<kit>", (match as TeamMatch).kit.name)
                        .replace("<time>", TimeUtil.millisToTimer(System.currentTimeMillis() - match.started)) }.collect(Collectors.toList())
            }

            return configFile.getStringList("scoreboard.match").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", PracticePlugin.instance.queueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString())
                    .replace("<opponent>", match.getOpponentString(player.uniqueId)!!)
                    .replace("<kit>", match.kit.name)
                    .replace("<time>", TimeUtil.millisToTimer(System.currentTimeMillis() - match.started)) }.collect(Collectors.toList())

        }

        if (profile?.state == ProfileState.QUEUE) {

            val queuePlayer = profile.queuePlayer

            if (queuePlayer == null) return mutableListOf()

            return configFile.getStringList("scoreboard.queue").stream()
                .map { CC.translate(it.replace("<online>", Bukkit.getOnlinePlayers().size.toString())
                    .replace("<queuing>", PracticePlugin.instance.queueManager.inQueue().toString()))
                    .replace("<in_match>", Match.inMatch().toString())
                    .replace("<time>", TimeUtil.millisToTimer(System.currentTimeMillis() - queuePlayer.started))}.collect(Collectors.toList())
        }

        return mutableListOf()
    }
}