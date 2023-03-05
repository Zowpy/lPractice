package net.lyragames.practice.queue.menu

import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.manager.QueueManager
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueueMenu(val ranked: Boolean): Menu() {

    override fun getTitle(player: Player?): String {
        return if (ranked) "Ranked Queue" else "Unranked Queue"
    }

    override fun isAutoUpdate(): Boolean {
        return true
    }

    override fun getButtons(player: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (queue in QueueManager.queues) {
            if (!queue.kit.kitData.enabled) continue
            if (ranked != queue.ranked) continue

            toReturn[toReturn.size] = QueueButton(queue, ranked)
        }

        return toReturn
    }
}