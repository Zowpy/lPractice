package net.lyragames.practice.profile

import net.lyragames.practice.constants.Constants
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.PlayerUtil
import net.lyragames.practice.utils.item.CustomItemStack
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

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

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        PlayerUtil.allowMovement(event.player)
        PlayerUtil.reset(event.player)

        val profile = Profile.getByUUID(event.player.uniqueId)
        profile?.state = ProfileState.LOBBY

        // Load in permissions and rank data to give player abilities like flight on join and other perks

        if (Constants.SPAWN != null) {
            event.player.teleport(Constants.SPAWN)
        }

        Hotbar.giveHotbar(Profile.getByUUID(event.player.uniqueId)!!)

        for (player in Bukkit.getOnlinePlayers()) {
            event.player.hidePlayer(player)
            player.hidePlayer(event.player)
        }

        val entityPlayer = (event.player as CraftPlayer).handle

        for (ffa in FFAManager.ffaMatches) {
            for (item in ffa.droppedItems) {
                val destroy = PacketPlayOutEntityDestroy(item.entityId)

                entityPlayer.playerConnection.sendPacket(destroy)
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.QUEUE) {

            if (profile.queuePlayer != null) {

                val queue = QueueManager.getQueue(player.uniqueId)
                queue?.queuePlayers?.remove(profile.queuePlayer)

                profile.state = ProfileState.LOBBY
                profile.queuePlayer = null
            }

        }

        if (profile?.state == ProfileState.MATCH) {

            if (profile.match != null) {

                val match = Match.getByUUID(profile.match!!)

                match?.handleQuit(match.getMatchPlayer(player.uniqueId)!!)
            }
        }

        if (profile?.state == ProfileState.SPECTATING) {

            if (profile.spectatingMatch != null) {

                val match = Match.getByUUID(profile.spectatingMatch!!)

                match?.removeSpectator(player)
            }
        }

        if (profile?.state == ProfileState.FFA) {
            val ffa = FFAManager.getByUUID(profile.ffa!!)

            ffa!!.handleLeave(ffa.getFFAPlayer(player.uniqueId)!!, true)
        }

        CustomItemStack.getCustomItemStacks().removeIf { it.uuid == player.uniqueId }
        Profile.profiles.remove(profile)
    }
}