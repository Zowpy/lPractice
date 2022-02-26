package net.lyragames.practice.queue.task

import net.lyragames.llib.utils.CC
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

object QueueTask: BukkitRunnable() {

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

                if (arena == null) {
                    queuePlayers.stream().map { Bukkit.getPlayer(it.uuid) }.forEach { it.sendMessage("${CC.RED}There is no free arenas!") }
                    continue
                }

                val match = Match(queue.kit, arena, queue.ranked)

                val pos = ThreadLocalRandom.current().nextInt(2)
                var indexed = 0

                for (queuePlayer in queuePlayers) {
                    if (indexed >= queue.requiredPlayers) continue

                    val player = Bukkit.getPlayer(queuePlayer.uuid) ?: continue

                    val profile = Profile.getByUUID(queuePlayer.uuid)

                    profile?.match = match.uuid
                    profile?.state = ProfileState.MATCH

                    if (indexed == 0) {
                        if (pos == 1) {
                            match.addPlayer(player, arena.l1!!)
                        }else {
                            match.addPlayer(player, arena.l2!!)
                        }
                    }else {
                        if (pos == 1) {
                            match.addPlayer(player, arena.l2!!)
                        }else {
                            match.addPlayer(player, arena.l1!!)
                        }
                    }

                    indexed++
                }

                queue.queuePlayers.removeAll(queuePlayers)
                Match.matches.add(match)

                match.start()
            }
        }
    }
}