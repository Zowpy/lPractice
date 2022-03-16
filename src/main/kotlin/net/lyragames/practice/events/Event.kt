package net.lyragames.practice.events

import org.bukkit.entity.Player
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/8/2022
 * Project: lPractice
 */

open class Event(val host: UUID) {

    val players: MutableList<EventPlayer> = mutableListOf()
    val round = 1

    var state = EventState.ANNOUNCING
    var type = EventType.SUMO

    fun getRemainingRounds(): Int {
        return players.stream().filter { !it.dead && !it.offline && round - it.roundsPlayed == 1 }.count().toInt()
    }

    open fun endRound() {

    }

    open fun startRound() {

    }

    open fun end() {

    }

    open fun addPlayer(player: Player) {
        val eventPlayer = EventPlayer(player.uniqueId)

        players.add(eventPlayer)
    }

    open fun canHit(player: Player, target: Player): Boolean {
        return true
    }

    fun getPlayer(uuid: UUID): EventPlayer? {
        return players.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }
}