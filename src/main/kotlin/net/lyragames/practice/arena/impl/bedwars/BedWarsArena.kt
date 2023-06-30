package net.lyragames.practice.arena.impl.bedwars

import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.type.ArenaType
import org.bukkit.Location


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/31/2022
 * Project: lPractice
 */

open class BedWarsArena(name: String) : Arena(name) {

    var redBed: Location? = null
    var blueBed: Location? = null

    var blueSpawn: Location? = null
    var redSpawn: Location? = null

    override var arenaType = ArenaType.BEDFIGHT
        get() = ArenaType.BEDFIGHT
}