package net.lyragames.practice.manager

import net.lyragames.practice.kit.Kit
import net.lyragames.practice.queue.Queue
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object QueueManager {

    val unrankedQueues: MutableList<Queue> = mutableListOf()
    val rankedQueues: MutableList<Queue> = mutableListOf()

    val queues: MutableList<Queue> = mutableListOf()

    fun load() {
        for (kit in Kit.kits) {
            val queue = Queue(kit, false)
            queues.add(queue)

            if (kit.kitData.ranked) {
                val queue1 = Queue(kit, true)
                queues.add(queue1)

                //rankedQueues.add(queue1)
            }
        }
    }

    fun getQueue(uuid: UUID): Queue? {
        return queues.stream().filter { queue ->
            queue.queuePlayers.stream().anyMatch { it.uuid == uuid }
        }.findFirst().orElse(null)
    }

    fun getByKit(kit: Kit): Queue? {
        return queues.stream().filter { it.kit.name.equals(kit.name, false) }
            .findFirst().orElse(null)
    }

    fun inQueue(): Int {
        var count = 0

        for (queue in queues) {
            count += queue.queuePlayers.size
        }

        return count
    }
}