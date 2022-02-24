package net.lyragames.practice.match.listener

import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 1/27/2022
 * Project: lPractice
 */
object MatchListener : Listener {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)
        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            if (match!!.kit.kitData.build) {
                match.blocksPlaced.add(event.blockPlaced)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)
        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            if (match!!.kit.kitData.build && match.blocksPlaced.contains(event.block)) {
                match.blocksPlaced.remove(event.block)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onLiquidPlace(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            if (match!!.kit.kitData.build) {
                match.blocksPlaced.add(event.blockClicked)
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onLiquidFill(event: PlayerBucketFillEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.kit.kitData.build && match.blocksPlaced.contains(event.blockClicked)) {
                match.blocksPlaced.remove(event.blockClicked)
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            match!!.droppedItems.add(event.itemDrop)
        }else {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickup(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.droppedItems.contains(event.item)) {
                match.droppedItems.remove(event.item)
            }else {
                event.isCancelled = true
            }
        }else {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onHit(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player

            val profile = Profile.getByUUID(player.uniqueId)
            val profile1 = Profile.getByUUID(damager.uniqueId)

            if (profile?.state != ProfileState.MATCH || profile1?.state != ProfileState.MATCH) {
                event.isCancelled = true
                return
            }

            if (profile.match?.equals(profile1.match)!!) {
                val match = Match.getByUUID(profile.match!!)

                val matchPlayer = match?.getMatchPlayer(player.uniqueId)
                val matchPlayer1 = match?.getMatchPlayer(damager.uniqueId)

                if (!match?.canHit(player, damager)!!) {
                    event.isCancelled = true
                }else {
                    matchPlayer?.lastDamager = damager.uniqueId
                    matchPlayer!!.hits++
                    matchPlayer.combo++

                    matchPlayer1?.combo = 0
                }

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        match.handleDeath(matchPlayer)
                    }
                }
            }
        }else if (event.entity is Player && event.damager == null || event.damager !is Player) {
            val player = event.entity as Player
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.match != null) {
                val match = Match.getByUUID(profile.match!!)

                val matchPlayer = match?.getMatchPlayer(player.uniqueId)
                matchPlayer?.lastDamager = null

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        match.handleDeath(matchPlayer)
                    }
                }
            }
        }
    }
}