package net.lyragames.practice.party.invitation

import java.util.*


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/22/2022
 * Project: lPractice
 */

class PartyInvitation(val uuid: UUID, val player: UUID) {

    val executedAt = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60000 * 5
    }
}