package net.lyragames.practice.events

import net.lyragames.llib.utils.CC
import net.lyragames.practice.events.map.EventMap
import net.lyragames.practice.events.player.EventPlayer
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
    val round = 1

    var state = EventState.ANNOUNCING
    var type = EventType.SUMO

    var playingPlayers: MutableList<EventPlayer> = mutableListOf()

    fun getRemainingRounds(): Int {
        return players.stream().filter { !it.dead && !it.offline && round - it.roundsPlayed == 1 }.count().toInt()
    }

    fun getNextPlayers(): MutableList<EventPlayer> {
        return players.stream().filter { !it.dead && !it.offline && round - it.roundsPlayed == 1 }
            .collect(Collectors.toList()).subList(0, 1)
    }

    open fun endRound(winner: EventPlayer?) {

    }

    open fun startRound() {

    }

    open fun end(winner: EventPlayer?) {

    }

    open fun addPlayer(player: Player) {
        val eventPlayer = EventPlayer(player.uniqueId)

        players.add(eventPlayer)
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