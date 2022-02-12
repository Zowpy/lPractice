package net.lyragames.practice.match.listener

import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 1/27/2022
 * Project: lPractice
 */
class MatchListener : Listener {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)
        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match)
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
            val match = Match.getByUUID(profile.match)
            if (match!!.kit.kitData.build && match.blocksPlaced.contains(event.block)) {
                event.isCancelled = false
                match.blocksPlaced.remove(event.block)
            } else {
                event.isCancelled = true
            }
        }
    }
}