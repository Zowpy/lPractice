package net.lyragames.practice.arena.impl.bedwars

import net.lyragames.practice.arena.Arena
import org.bukkit.Location


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 3/31/2022
 * Project: lPractice
 */

class BedWarsArena(name: String) : Arena(name) {

    var redBed: Location? = null
    var blueBed: Location? = null

    var blueSpawn: Location? = null
    var redSpawn: Location? = null
}