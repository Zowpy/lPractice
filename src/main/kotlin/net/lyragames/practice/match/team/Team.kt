package net.lyragames.practice.match.team

import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.utils.Cuboid
import net.lyragames.practice.utils.title.TitleBar
import org.bukkit.Location
import java.util.*

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class Team(val name: String) {
    val uuid: UUID = UUID.randomUUID()
    val players: MutableList<TeamMatchPlayer> = mutableListOf()

    var coloredName = name
    var color = ""

    var spawn: Location? = null
    var bedLocation: Location? = null
    var portal: Cuboid? = null

    var broken = false

    var hits = 0
    var points = 0

    fun alivePlayers(): Int {
        return players.count { !it.offline && !it.dead }
    }

    fun sendTitle(title: String, subtitle: String, fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20) {
        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            TitleBar.sendTitleBar(matchPlayer.player, title, subtitle, fadeIn, stay, fadeOut)
        }
    }
}