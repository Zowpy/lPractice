package net.lyragames.practice.match.player

import net.lyragames.llib.utils.Countdown
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

open class MatchPlayer(val uuid: UUID, val name: String, var spawn: Location) {
    var dead = false
    var offline = false
    val player: Player
        get() = Bukkit.getPlayer(uuid)

    var lastDamager: UUID? = null

    var hits = 0
    var combo = 0
    var longestCombo = 0

    var comboed = 0

    var potionsThrown = 0
    var potionsMissed = 0

    // bed fights & mlgrush
    var points = 0
    var bed: Location? = null
    var respawnCountdown: Countdown? = null
}