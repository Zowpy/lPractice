package net.lyragames.practice.listener

import net.lyragames.practice.constants.Constants
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.PlayerUtil
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object MoveListener: Listener {

    @EventHandler(ignoreCancelled = true)
    fun onMove(event: PlayerMoveEvent) {
        if (event.from.x == event.to.x
            && event.from.y == event.to.y
            && event.from.z == event.to.z) return

        val player = event.player

        if (PlayerUtil.denyMovement.contains(player.uniqueId)) {
            player.teleport(event.from)
            return
        }

        val profile = Profile.getByUUID(player.uniqueId)

        if (profile!!.state == ProfileState.LOBBY || profile.state == ProfileState.QUEUE) {
            if (event.to.y <= 2) {
                if (Constants.SPAWN != null) {
                    event.player.teleport(Constants.SPAWN)
                }
            }

            return
        }

        if (profile.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match != null) {
                if (match.kit.kitData.mlgRush || match.kit.kitData.bedFights || match.kit.kitData.bridge || match.kit.kitData.fireballFight) {

                    if (event.to.y <= match.arena.deadzone) {
                        val matchPlayer = match.getMatchPlayer(player.uniqueId)

                        if (matchPlayer!!.respawning) {
                            player.teleport(match.arena.bounds.center)
                            return
                        }

                        if (!matchPlayer.dead && match.matchState == MatchState.FIGHTING) {
                            matchPlayer.lastDamager = null

                            match.handleDeath(matchPlayer)
                        } else {
                            player.teleport(match.arena.bounds.center)
                        }
                    }
                }
            }
        }

        if (event.to.block.type == Material.WATER || event.to.block.type == Material.STATIONARY_WATER) {

            if (profile.state == ProfileState.MATCH) {

                val match = Match.getByUUID(profile.match!!)
                val matchPlayer = match!!.getMatchPlayer(player.uniqueId)

                if (!matchPlayer!!.dead && match.matchState == MatchState.FIGHTING && match.kit.kitData.sumo) {
                    match.handleDeath(match.getMatchPlayer(player.uniqueId)!!)
                }

            } else if (profile.state == ProfileState.EVENT) {

                val currentEvent = EventManager.event ?: return
                if (currentEvent.state != EventState.FIGHTING) return

                if (currentEvent.type == EventType.SUMO) {

                    val eventPlayer = currentEvent.getPlayer(player.uniqueId)

                    if (currentEvent.playingPlayers.stream().noneMatch { it.uuid == player.uniqueId }) return

                    eventPlayer?.dead = true

                    currentEvent.endRound(currentEvent.getOpponent(eventPlayer!!))
                }
            }
        }
    }
}