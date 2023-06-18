package net.lyragames.practice.match.impl

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.profile.Profile
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/29/2022
 * Project: lPractice
 */

class MLGRushMatch(kit: Kit, arena: Arena, ranked: Boolean) : TeamMatch(kit, arena, ranked) {

    private var round = 1

    fun handleBreak(event: BlockBreakEvent) {

        if (matchState != MatchState.FIGHTING) {
            event.isCancelled = true
            return
        }

        val player = getMatchPlayer(event.player.uniqueId)

        if (player?.dead!! || player.respawning) {
            event.isCancelled = true
            return
        }

        if (blocksPlaced.contains(event.block)) {
            blocksPlaced.remove(event.block)
        } else {
            event.isCancelled = true
        }

        if (event.block.type == Material.BED || event.block.type == Material.BED_BLOCK) {
            for (matchPlayer in players) {
                for (x in event.block.x - 2 until event.block.x + 2) {
                    for (y in event.block.y - 2 until event.block.y + 2) {
                        for (z in event.block.z - 2 until event.block.z + 2) {
                            if (matchPlayer.bed?.blockX == x && matchPlayer.bed?.blockY == y && matchPlayer.bed?.blockZ == z) {

                                if (player?.bed?.blockX == x && player.bed?.blockY == y && player.bed?.blockZ == z) {
                                    player.player.sendMessage("${CC.RED}You cannot break your own bed.")
                                    event.isCancelled = true
                                    break
                                }

                                event.isCancelled = true
                                player.points++
                                resetMatch(player as TeamMatchPlayer)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleDeath(player: MatchPlayer) {
        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has died from natural causes!")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        player.respawning = true

        PlayerUtil.reset(player.player)

        player.player.allowFlight = true
        player.player.isFlying = true

        players.stream().forEach { if (it.player != null) it.player.hidePlayer(player.player) }

        player.player.teleport(player.spawn.clone().add(0.0, 5.0, 0.0))

        player.respawnCountdown = Countdown(PracticePlugin.instance, player.player, "${CC.PRIMARY}Respawning in ${CC.SECONDARY}<seconds>${CC.PRIMARY}!", 6) {
            val profile = Profile.getByUUID(player.uuid)
            player.player.teleport(player.spawn)

            PlayerUtil.reset(player.player)

            profile?.getKitStatistic(kit.name)?.generateBooks(player.player)

            player.respawning = false

            players.stream().forEach { if (!it.offline) it.player.showPlayer(player.player) }

            player.respawnCountdown = null
        }
    }

    private fun resetMatch(winner: TeamMatchPlayer) {
        if (winner.points >= 5) {
            end(getOpponent(winner.uuid) as TeamMatchPlayer)
        }else {
            round++

            countdowns.forEach { it.cancel() }

            reset()
            matchState = MatchState.STARTING

            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                if (matchPlayer.respawnCountdown != null) {
                    matchPlayer.respawnCountdown?.cancel()
                    matchPlayer.respawnCountdown?.consumer?.accept(true)
                    matchPlayer.respawnCountdown = null
                }

                val player = matchPlayer.player
                val profile = Profile.getByUUID(player.uniqueId)

                PlayerUtil.reset(player)
                PlayerUtil.denyMovement(player)

                player.teleport(matchPlayer.spawn)
                profile?.getKitStatistic(kit.name)?.generateBooks(player)

                countdowns.add(Countdown(
                    PracticePlugin.instance,
                    player,
                    "${CC.PRIMARY}Round ${CC.SECONDARY}$round ${CC.PRIMARY}starting in ${CC.SECONDARY}<seconds>${CC.PRIMARY} seconds!",
                    6
                ) {
                    player.sendMessage("${CC.PRIMARY}Round started!")
                    matchState = MatchState.FIGHTING
                    PlayerUtil.allowMovement(player)
                })
            }

        }
    }

    private fun end(player: TeamMatchPlayer) {
        end(getTeam(player.teamUniqueId)!!.players.map { getMatchPlayer(it.uuid)!! }.toMutableList())
    }

    override fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

       // handleDeath(matchPlayer)

        if (teams.stream().anyMatch { team -> team.players.stream().noneMatch { !it.offline } }) {
            end(players.stream().filter { !it.offline }.findAny().orElse(null) as TeamMatchPlayer)
        }
    }
}