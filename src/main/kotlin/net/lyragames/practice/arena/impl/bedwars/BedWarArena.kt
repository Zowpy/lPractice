package net.lyragames.practice.arena.impl.bedwars

import net.lyragames.practice.arena.Arena
import org.bukkit.Location


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/28/2022
 * Project: lPractice
 */

class BedWarArena(name: String) : Arena(name) {

    var bed1: Location? = null
    var bed2: Location? = null
}