package net.lyragames.practice.queue.menu

import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.PracticePlugin
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueueMenu(val ranked: Boolean): Menu() {

    override fun getTitle(player: Player?): String {
        return if (ranked) "Ranked" else "Unranked"
    }

    override fun getButtons(player: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        var i = 0
        for (queue in PracticePlugin.instance.queueManager.queues) {
            if (ranked != queue.ranked) continue

            toReturn[i] = QueueButton(queue, ranked)
            i++
        }

        return toReturn
    }
}