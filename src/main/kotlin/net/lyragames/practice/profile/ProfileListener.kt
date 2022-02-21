package net.lyragames.practice.profile

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

object ProfileListener: Listener {

    @EventHandler
    fun onAsyncLogin(event: AsyncPlayerPreLoginEvent) {
        try {
            val profile = Profile(event.uniqueId, event.name)
            profile.load()
            Profile.profiles.add(profile)
        } catch (e: Exception) {
            event.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
            event.kickMessage = CC.RED + "Failed to load your profile!"
            e.printStackTrace()
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerUtil.reset(event.player)
        Hotbar.giveHotbar(Profile.getByUUID(event.player.uniqueId)!!)
    }
}