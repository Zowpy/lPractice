package net.lyragames.practice.queue.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.manager.MatchManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.PlayerUtil
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object QueueTask : BukkitRunnable() {

    init {
        runTaskTimer(PracticePlugin.instance, 40L, 40L)
    }

    override fun run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return

        try {
            for (queue in QueueManager.queues) {

                if (queue.queuePlayers.isEmpty()) continue

                queue.queuePlayers.forEach { it.tickRange() }

                if (queue.queuePlayers.size < 2) {
                    continue
                }

                for (firstQueueProfile in queue.queuePlayers) {
                    val firstPlayer = Bukkit.getPlayer(firstQueueProfile.uuid) ?: continue

                    for (secondQueueProfile in queue.queuePlayers) {
                        if (firstQueueProfile.uuid == secondQueueProfile.uuid) {
                            continue
                        }

                        val secondPlayer = Bukkit.getPlayer(secondQueueProfile.uuid) ?: continue

                        if (secondQueueProfile.pingFactor != 0 && PlayerUtil.getPing(firstPlayer) > secondQueueProfile.pingFactor ||
                            firstQueueProfile.pingFactor != 0 && PlayerUtil.getPing(secondPlayer) > firstQueueProfile.pingFactor
                        ) {
                            continue
                        }

                        if (queue.ranked) {
                            if (!firstQueueProfile.isInRange(secondQueueProfile.elo) || !secondQueueProfile.isInRange(
                                    firstQueueProfile.elo
                                )
                            ) {
                                continue
                            }
                        }

                        val arena = ArenaManager.getFreeArena(queue.kit)
                            ?: /*arrayOf(
                                                    firstPlayer,
                                                    secondPlayer
                                                ).forEach { it.sendMessage("${CC.RED}There are no free arenas!") } */
                            continue

                        queue.queuePlayers.remove(firstQueueProfile)
                        queue.queuePlayers.remove(secondQueueProfile)

                        val profile = Profile.getByUUID(firstPlayer.uniqueId)
                        val profile1 = Profile.getByUUID(secondPlayer.uniqueId)

                        val match = MatchManager.createMatch(
                            queue.kit,
                            arena,
                            queue.ranked,
                            false,
                            firstPlayer,
                            secondPlayer
                        )

                        for (uuid in profile?.followers!!) {
                            val playerProfile = Profile.getByUUID(uuid)

                            playerProfile?.silent = true
                            match.addSpectator(playerProfile?.player!!)
                            playerProfile.player.teleport(firstPlayer.location)
                        }

                        for (uuid in profile1?.followers!!) {
                            if (firstPlayer.uniqueId == uuid || secondPlayer.uniqueId == uuid) continue

                            val playerProfile = Profile.getByUUID(uuid)

                            playerProfile?.silent = true
                            match.addSpectator(playerProfile?.player!!)
                            playerProfile.player.teleport(secondPlayer.location)
                        }
                    }
                }
            }
        } catch (ignored: ConcurrentModificationException) {
        }
    }
}