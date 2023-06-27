package net.lyragames.practice.queue.menu

import net.lyragames.llib.utils.CC
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.Glass
import net.lyragames.practice.manager.QueueManager
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
        return if (ranked) "${CC.SECONDARY}${CC.BOLD}Ranked Queue" else "${CC.SECONDARY}${CC.BOLD}Unranked Queue"
    }

    override fun isAutoUpdate(): Boolean {
        return true
    }

    override fun getSize(): Int {
        return 45
    }

    override fun getButtons(player: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        var queueIndex = 0

        for (i in 0 until 45) {
            if (BLACKLISTED_SLOTS.contains(i)) continue
            //if (QueueManager.queues.size <= queueIndex) break

            val queue = QueueManager.queues[queueIndex]

            if (!queue.kit.kitData.enabled) continue
            if (ranked != queue.ranked) continue

            queueIndex++

            toReturn[i] = QueueButton(queue, ranked)
        }

        for (int in BLACKLISTED_SLOTS) {
            toReturn[int] = Glass()
        }

        return toReturn
    }

    companion object {
        val BLACKLISTED_SLOTS: List<Int> = listOf(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            17, 18, 26, 27, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44
        )
    }
}