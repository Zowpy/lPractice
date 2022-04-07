package net.lyragames.practice.queue.task

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.impl.mlgrush.MLGRushArena
import net.lyragames.practice.arena.impl.mlgrush.StandaloneMLGRushArena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.impl.BedFightMatch
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

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

      /*  for (queue in QueueManager.queues) {
            if (queue.queuePlayers.isEmpty()) continue

            if (queue.queuePlayers.size >= queue.requiredPlayers) {
                val queuePlayers = queue.queuePlayers.subList(0, queue.requiredPlayers)

                val arena = ArenaManager.getFreeArena()

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
        } */

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
                                firstQueueProfile.pingFactor != 0 && PlayerUtil.getPing(secondPlayer) > firstQueueProfile.pingFactor) {
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

                        if (arena == null) {
                            arrayOf(
                                firstPlayer,
                                secondPlayer
                            ).forEach { it.sendMessage("${CC.RED}There are no free arenas!") }
                            continue
                        }

                        arena.free = false

                        queue.queuePlayers.remove(firstQueueProfile)
                        queue.queuePlayers.remove(secondQueueProfile)

                        var match = Match(queue.kit, arena, queue.ranked)

                        if (queue.kit.kitData.mlgRush) {
                            match = MLGRushMatch(queue.kit, arena, queue.ranked)
                        }else if (queue.kit.kitData.bedFights) {
                            match = BedFightMatch(queue.kit, arena, queue.ranked)
                        }

                        val profile = Profile.getByUUID(firstPlayer.uniqueId)
                        val profile1 = Profile.getByUUID(secondPlayer.uniqueId)

                        profile?.match = match.uuid
                        profile?.state = ProfileState.MATCH

                        profile1?.match = match.uuid
                        profile1?.state = ProfileState.MATCH

                        if (arena is StandaloneBedWarsArena) {
                            match.addPlayer(firstPlayer, arena.blueSpawn!!)
                            match.addPlayer(secondPlayer, arena.redSpawn!!)

                            match.getMatchPlayer(firstPlayer.uniqueId)?.bed = arena.blueBed
                            match.getMatchPlayer(secondPlayer.uniqueId)?.bed = arena.redBed
                        }else if (arena is BedWarsArena) {
                            match.addPlayer(firstPlayer, arena.blueSpawn!!)
                            match.addPlayer(secondPlayer, arena.redSpawn!!)

                            match.getMatchPlayer(firstPlayer.uniqueId)?.bed = arena.blueBed
                            match.getMatchPlayer(secondPlayer.uniqueId)?.bed = arena.redBed
                        } else if (arena is StandaloneMLGRushArena) {
                            match.addPlayer(firstPlayer, arena.l1!!)
                            match.addPlayer(secondPlayer, arena.l2!!)

                            match.getMatchPlayer(firstPlayer.uniqueId)?.bed = arena.bed1
                            match.getMatchPlayer(secondPlayer.uniqueId)?.bed = arena.bed2
                        }else if (arena is MLGRushArena) {
                            match.addPlayer(firstPlayer, arena.l1!!)
                            match.addPlayer(secondPlayer, arena.l2!!)

                            match.getMatchPlayer(firstPlayer.uniqueId)?.bed = arena.bed1
                            match.getMatchPlayer(secondPlayer.uniqueId)?.bed = arena.bed2
                        } else {
                            match.addPlayer(firstPlayer, arena.l1!!)
                            match.addPlayer(secondPlayer, arena.l2!!)
                        }

                        generateMessage(firstPlayer, secondPlayer, queue.ranked, arena, queue.kit)

                        Match.matches.add(match)

                        match.start()

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
        }catch (ignored: ConcurrentModificationException) {}
    }

    private fun generateMessage(firstPlayer: Player, secondPlayer: Player, ranked: Boolean, arena: Arena, kit: Kit) {
        firstPlayer.sendMessage(" ")
        secondPlayer.sendMessage(" ")

        firstPlayer.sendMessage("${CC.PRIMARY}${CC.BOLD}${if (ranked) "Ranked" else "Unranked"} Match")
        secondPlayer.sendMessage("${CC.PRIMARY}${CC.BOLD}${if (ranked) "Ranked" else "Unranked"} Match")

        firstPlayer.sendMessage("${CC.PRIMARY} ⚫ Map: ${CC.SECONDARY}${arena.name}")
        firstPlayer.sendMessage("${CC.PRIMARY} ⚫ Opponent: ${CC.RED}${secondPlayer.name}")
        firstPlayer.sendMessage("${CC.PRIMARY} ⚫ Ping: ${CC.RED}${PlayerUtil.getPing(secondPlayer)} ms")

        secondPlayer.sendMessage("${CC.PRIMARY} ⚫ Map: ${CC.SECONDARY}${arena.name}")
        secondPlayer.sendMessage("${CC.PRIMARY} ⚫ Opponent: ${CC.RED}${firstPlayer.name}")
        secondPlayer.sendMessage("${CC.PRIMARY} ⚫ Ping: ${CC.RED}${PlayerUtil.getPing(firstPlayer)} ms")

        if (ranked) {
            val profile = Profile.getByUUID(firstPlayer.uniqueId)
            val profile1 = Profile.getByUUID(secondPlayer.uniqueId)

            secondPlayer.sendMessage("${CC.PRIMARY} ⚫ ELO: ${CC.SECONDARY}${profile?.getKitStatistic(kit.name)?.elo}")
            firstPlayer.sendMessage("${CC.PRIMARY} ⚫ ELO: ${CC.SECONDARY}${profile1?.getKitStatistic(kit.name)?.elo}")
        }

        firstPlayer.sendMessage(" ")
        secondPlayer.sendMessage(" ")
    }
}