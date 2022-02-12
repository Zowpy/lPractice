package net.lyragames.practice.match.team

import net.lyragames.practice.match.player.TeamMatchPlayer
import java.util.*

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class Team {
    val uuid = UUID.randomUUID()
    val players: List<TeamMatchPlayer> = ArrayList()
}