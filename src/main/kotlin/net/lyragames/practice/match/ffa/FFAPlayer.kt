package net.lyragames.practice.match.ffa

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/26/2022
 * Project: lPractice
 */

class FFAPlayer(val uuid: UUID, name: String) {

    val player: Player get() = Bukkit.getPlayer(uuid)

    var kills = 0
    var killStreak = 0
    var death = 0
}