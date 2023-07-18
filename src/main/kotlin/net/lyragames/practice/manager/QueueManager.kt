package net.lyragames.practice.manager

import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.queue.Queue
import net.lyragames.practice.queue.QueuePlayer
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

    fun addToQueue(profile: Profile, queue: Queue) {
        val queuePlayer = QueuePlayer(profile.uuid, profile.name!!, queue, profile.settings.pingRestriction)
        queuePlayer.elo = profile.getKitStatistic(queue.kit.name)?.elo!!

        profile.queuePlayer = queuePlayer
        profile.state = ProfileState.QUEUE

        queue.queuePlayers.add(queuePlayer)
    }

    fun findQueue(kit: Kit, ranked: Boolean): Queue? {
        return queues.firstOrNull { it.kit.name == kit.name && it.ranked == ranked }
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