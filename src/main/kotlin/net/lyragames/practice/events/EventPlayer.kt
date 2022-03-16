package net.lyragames.practice.events

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/8/2022
 * Project: lPractice
 */

class EventPlayer(val uuid: UUID) {

    val player: Player
      get() = Bukkit.getPlayer(uuid)

    var dead = false
    var offline = false

    var roundsPlayed = 0
}