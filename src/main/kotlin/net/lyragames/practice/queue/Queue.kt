package net.lyragames.practice.queue

import net.lyragames.practice.kit.Kit


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class Queue(var kit: Kit, var ranked: Boolean) {

    val queuePlayers: MutableList<QueuePlayer> = mutableListOf()
    var requiredPlayers: Int = 2

}