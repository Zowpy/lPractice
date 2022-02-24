package net.lyragames.practice.party

import net.lyragames.llib.utils.CC
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/22/2022
 * Project: lPractice
 */

class Party(val leader: UUID) {
    
    val uuid: UUID = UUID.randomUUID()

    var partyType = PartyType.PRIVATE
    val players: MutableList<UUID> = mutableListOf()
    val banned: MutableList<UUID> = mutableListOf()

    fun sendMessage(message: String) {
        players.stream().map { Bukkit.getPlayer(it) }
            .forEach { it.sendMessage(CC.translate(message)) }
    }
}