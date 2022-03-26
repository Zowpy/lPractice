package net.lyragames.practice.event.impl

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.event.Event
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.map.impl.TNTRunMap
import net.lyragames.practice.event.player.EventPlayer
import net.lyragames.practice.event.player.EventPlayerState
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/26/2022
 * Project: lPractice
 */

class TNTRunEvent(host: UUID, eventMap: EventMap) : Event(host, eventMap) {

    val removedBlocks: MutableMap<Block, Material> = mutableMapOf()

    override fun startRound() {
        eventMap as TNTRunMap
        state = EventState.STARTING

        for (eventPlayer in players) {
            eventPlayer.state = EventPlayerState.FIGHTING

            PlayerUtil.reset(eventPlayer.player)
            eventPlayer.player.teleport(eventMap.spawn)
        }

        for (eventPlayer in players) {
            if (eventPlayer.offline) continue

            Countdown(
                PracticePlugin.instance,
                eventPlayer.player,
                "&aGame starting in <seconds> seconds!",
                6
            ) {
                eventPlayer.player.sendMessage("${CC.GREEN}Game started!")
                state = EventState.FIGHTING
            }
        }

    }

    override fun endRound(winner: EventPlayer?) {
        state = EventState.ENDING

        for (eventPlayer in playingPlayers) {
            eventPlayer.state = EventPlayerState.LOBBY

            Hotbar.giveHotbar(Profile.getByUUID(eventPlayer.uuid)!!)
            PlayerUtil.reset(eventPlayer.player)
        }

        end(winner)
    }

    override fun end(winner: EventPlayer?) {
        Bukkit.broadcastMessage("${CC.GREEN}${if (winner != null) winner.player.name else "N/A"} won the event!")
        players.forEach {
            forceRemove(it.player)
        }

        reset()
        EventManager.event = null
    }

    fun reset() {
        for (entry in removedBlocks) {
            entry.key.type = entry.value
        }
    }
}