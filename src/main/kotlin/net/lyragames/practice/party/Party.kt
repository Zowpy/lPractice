package net.lyragames.practice.party

import lombok.Getter
import lombok.Setter
import net.lyragames.llib.utils.CC
import net.lyragames.practice.party.invitation.PartyInvitation
import org.bukkit.Bukkit
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/22/2022
 * Project: lPractice
 */

class Party(var leader: UUID) {

    val uuid: UUID = UUID.randomUUID()
    var MAX_SIZE: Int = 30

    var partyType = PartyType.PRIVATE
    var partyModerators: MutableList<UUID> = mutableListOf()
    val players: MutableList<UUID> = mutableListOf()
    val banned: MutableList<UUID> = mutableListOf()




    fun sendMessage(message: String) {
        players.stream().map { Bukkit.getPlayer(it) }
            .forEach { it.sendMessage(CC.translate(message)) }
    }
}