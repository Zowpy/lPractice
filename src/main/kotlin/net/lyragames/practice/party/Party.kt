package net.lyragames.practice.party

import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/22/2022
 * Project: lPractice
 */

class Party {
    
    val uuid: UUID = UUID.randomUUID()
    var leader: UUID? = null

    val players: MutableList<UUID> = mutableListOf()

}