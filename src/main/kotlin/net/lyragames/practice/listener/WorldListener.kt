package net.lyragames.practice.listener

import net.lyragames.practice.profile.Profile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.weather.WeatherChangeEvent

object WorldListener : Listener {

    @EventHandler
    fun weatherChange(event: WeatherChangeEvent) {
        if (event.toWeatherState()) event.isCancelled = true
    }

    @EventHandler
    fun leavesChange(event: LeavesDecayEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun hangingBreak(event: HangingBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun blockBurn(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun interact(event: PlayerInteractEvent) {
        val profile = Profile.getByUUID(event.player.uniqueId)

        event.isCancelled = !(profile?.canBuild == true && profile?.match == null)
    }

}