package net.lyragames.practice.queue

import net.lyragames.practice.Locale
import net.lyragames.practice.utils.CC
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueuePlayer(var uuid: UUID, var name: String, val queue: Queue, val pingFactor: Int) {

    var elo = 0
    val started = System.currentTimeMillis()

    private var range = 25
    private var ticked = 0

    fun tickRange() {
        ticked++
        if (ticked % 6 == 0) {
            range += 3
            if (ticked >= 50) {
                ticked = 0
                if (queue.ranked) {
                    Bukkit.getPlayer(uuid)?.sendMessage(Locale.ELO_SEARCH.getMessage().replace("<min>", "${getMinRange()}").replace("<max>", "${getMaxRange()}"))
                }
            }
        }
    }

    fun isInRange(elo: Int): Boolean {
        return elo >= this.elo - range && elo <= this.elo + range
    }


    fun getMinRange(): Int {
        val min = elo - range
        return Math.max(min, 0)
    }

    fun getMaxRange(): Int {
        val max = elo + range
        return Math.min(max, 2500)
    }
}