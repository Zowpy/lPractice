package net.lyragames.practice.manager

import net.lyragames.practice.kit.Kit
import net.lyragames.practice.queue.Queue


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object QueueManager {

    val queues: MutableList<Queue> = mutableListOf()

    fun load() {
        for (kit in Kit.kits) {
            val queue = Queue(kit, false)
            queues.add(queue)

            if (kit.kitData.ranked) {
                val queue1 = Queue(kit, true)
                queues.add(queue1)
            }
        }
    }

    fun inQueue(): Int {
        var count = 0

        for (queue in queues) {
            count += queue.queuePlayers.size
        }

        return count
    }
}