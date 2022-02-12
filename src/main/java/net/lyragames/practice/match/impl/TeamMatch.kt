package net.lyragames.practice.match.impl

import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.team.Team
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Stream


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

    override fun canHit(player: Player, target: Player): Boolean {
        val teamMatchPlayer = getMatchPlayer(player.uniqueId) as TeamMatchPlayer
        val teamMatchPlayer1 = getMatchPlayer(target.uniqueId) as TeamMatchPlayer

        return teamMatchPlayer.teamUniqueId != teamMatchPlayer1.teamUniqueId
    }

    private fun findTeam(): Team? {
        val stream: Stream<Team> = teams.stream().sorted(Comparator.comparingInt{ team: Team -> team.players.size })
        return stream.findFirst().orElse(stream.findAny().get())
    }

    override fun addPlayer(player: Player, location: Location) {
        val teamMatchPlayer = findTeam()?.uuid?.let { TeamMatchPlayer(player.uniqueId, player.name, location, it) }
        if (teamMatchPlayer != null) {
            players.add(teamMatchPlayer)
        }
    }
}