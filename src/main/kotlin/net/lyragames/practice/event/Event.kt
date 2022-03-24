package net.lyragames.practice.event

import net.lyragames.llib.utils.CC
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.player.EventPlayer
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import java.util.*
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/8/2022
 * Project: lPractice
 */

open class Event(val host: UUID, val eventMap: EventMap) {

    val players: MutableList<EventPlayer> = mutableListOf()
    var round = 1

    var state = EventState.ANNOUNCING
    var type = EventType.SUMO
    var requiredPlayers = 32

    var created = System.currentTimeMillis()

    val droppedItems: MutableList<Item> = mutableListOf()
    var playingPlayers: MutableList<EventPlayer> = mutableListOf()

    fun getRemainingRounds(): Int {
        return players.stream().filter { !it.dead && !it.offline && round - it.roundsPlayed <= 1 }.count().toInt() / 2
    }

    fun getNextPlayers(): MutableList<EventPlayer> {
        return players.stream().filter { !it.dead && !it.offline && round - it.roundsPlayed <= 1 }
            .collect(Collectors.toList()).subList(0, 2)
    }

    open fun endRound(winner: EventPlayer?) {

    }

    open fun startRound() {

    }

    open fun end(winner: EventPlayer?) {

    }

    open fun getOpponent(eventPlayer: EventPlayer): EventPlayer? {
        return playingPlayers.stream().filter { it.uuid != eventPlayer.uuid }
            .findFirst().orElse(null)
    }

    open fun addPlayer(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        val eventPlayer = EventPlayer(player.uniqueId)

        profile?.state = ProfileState.EVENT

        players.forEach {
            it.player.showPlayer(player)
            player.showPlayer(it.player)
        }

        player.teleport(eventMap.spawn)

        players.add(eventPlayer)
        Hotbar.giveHotbar(profile!!)

        Bukkit.broadcastMessage("${CC.GREEN}${player.name}${CC.YELLOW} has joined the event. ${CC.GRAY}(${players.size}/${requiredPlayers})")
    }

    open fun removePlayer(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        players.removeIf { it.uuid == player.uniqueId }

        players.forEach {
            it.player.hidePlayer(player)
            player.hidePlayer(it.player)
        }

        if (Constants.SPAWN != null) {
            player.teleport(Constants.SPAWN)
        }

        profile?.state = ProfileState.LOBBY
        Hotbar.giveHotbar(profile!!)
    }

    open fun canHit(player: Player, target: Player): Boolean {
        return true
    }

    fun sendMessage(message: String) {
        players.stream().filter { !it.offline }
            .forEach {
                it.player.sendMessage(CC.translate(message))
            }
    }

    fun getPlayer(uuid: UUID): EventPlayer? {
        return players.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }
}