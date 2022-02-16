package net.lyragames.practice.queue.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ThreadLocalRandom

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueueTask: BukkitRunnable() {

    init {
        runTaskTimer(PracticePlugin.instance, 40L, 40L)
    }

    override fun run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return

        for (queue in PracticePlugin.instance.queueManager.queues) {
            if (queue.queuePlayers.isEmpty()) continue

            if (queue.queuePlayers.size >= queue.requiredPlayers) {
                val queuePlayers = queue.queuePlayers.subList(0, queue.requiredPlayers)

                val arena = PracticePlugin.instance.arenaManager.getFreeArena()
                val match = arena?.let { Match(queue.kit, it, queue.ranked) } ?: continue

                val pos = ThreadLocalRandom.current().nextInt(2)
                var indexed = 0

                for (queuePlayer in queuePlayers) {
                    val player = Bukkit.getPlayer(queuePlayer.uuid) ?: continue

                    val profile = Profile.getByUUID(queuePlayer.uuid)

                    profile?.match = match.uuid
                    profile?.state = ProfileState.MATCH

                    if (indexed == 0) {
                        if (pos == 1) {
                            arena.l1?.let { match.addPlayer(player, it) }
                        }else {
                            arena.l2?.let { match.addPlayer(player, it) }
                        }
                    }else {
                        if (pos == 1) {
                            arena.l2?.let { match.addPlayer(player, it) }
                        }else {
                            arena.l1?.let { match.addPlayer(player, it) }
                        }
                    }

                    indexed++
                }

                Match.matches.add(match)
                match.start()
            }
        }
    }
}