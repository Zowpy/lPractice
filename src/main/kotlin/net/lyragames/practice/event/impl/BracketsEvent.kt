package net.lyragames.practice.event.impl

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.event.Event
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.player.EventPlayer
import net.lyragames.practice.event.player.EventPlayerState
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/21/2022
 * Project: lPractice
 */

class BracketsEvent(host: UUID, eventMap: EventMap, val kit: Kit) : Event(host, eventMap) {

    init {
        type = EventType.BRACKETS
    }

    val blocksPlaced: MutableList<Block> = mutableListOf()

    override fun startRound() {
        state = EventState.STARTING

        playingPlayers = getNextPlayers()

        for ((i, eventPlayer) in playingPlayers.withIndex()) {
            eventPlayer.roundsPlayed++
            eventPlayer.state = EventPlayerState.FIGHTING

            val profile = Profile.getByUUID(eventPlayer.uuid)

            PlayerUtil.reset(eventPlayer.player)

            profile?.getKitStatistic(kit.name)?.generateBooks(eventPlayer.player)

            if (i == 0) {
                eventPlayer.player.teleport(eventMap.l1)
            }else {
                eventPlayer.player.teleport(eventMap.l2)
            }

            PlayerUtil.denyMovement(eventPlayer.player)
        }

        for (eventPlayer in players) {
            if (eventPlayer.offline) continue

            Countdown(
                PracticePlugin.instance,
                eventPlayer.player,
                "&aRound $round starting in <seconds> seconds!",
                6
            ) {
                eventPlayer.player.sendMessage(CC.GREEN + "Round started!")
                state = EventState.FIGHTING

                if (playingPlayers.contains(eventPlayer)) {
                    PlayerUtil.allowMovement(eventPlayer.player)
                }
            }
        }

    }

    override fun endRound(winner: EventPlayer?) {
        state = EventState.ENDING

        for (eventPlayer in playingPlayers) {
            eventPlayer.state = EventPlayerState.LOBBY

            eventPlayer.player.teleport(eventMap.spawn)
            Hotbar.giveHotbar(Profile.getByUUID(eventPlayer.uuid)!!)
            PlayerUtil.reset(eventPlayer.player)
        }

        reset()

        if (getRemainingRounds() == 0) {
            end(winner)
        }else {
            startRound()
        }
    }

    override fun end(winner: EventPlayer?) {
        Bukkit.broadcastMessage("${CC.GREEN}${if (winner != null) winner.player.name else "N/A"} won the event!")
        players.forEach { player ->
            val profile = Profile.getByUUID(player.uuid)

            players.forEach {
                it.player.hidePlayer(player.player)
                player.player.hidePlayer(it.player)
            }

            profile?.state = ProfileState.LOBBY
            Hotbar.giveHotbar(profile!!)
        }
        EventManager.event = null
    }

    fun reset() {
        blocksPlaced.forEach { it.type = Material.AIR }
        droppedItems.forEach { it.remove() }
    }
}