package net.lyragames.practice.match.team

import net.lyragames.practice.match.player.TeamMatchPlayer
import org.bukkit.Location
import java.util.*

/**
 * This Project is property of Zowpy & EliteAres © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 12/19/2021
 * Project: Practice
 */

class Team(val name: String) {
    val uuid = UUID.randomUUID()
    val players: MutableList<TeamMatchPlayer> = mutableListOf()

    var spawn: Location? = null
    var bedLocation: Location? = null

    var broken = false

    var hits = 0
}