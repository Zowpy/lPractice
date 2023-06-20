package net.lyragames.practice.arena.impl.bridge

import net.lyragames.llib.utils.Cuboid
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.type.ArenaType
import org.bukkit.Location

class BridgeArena(name: String) : Arena(name) {

    var redSpawn: Location? = null
    var redPortal1: Location? = null
    var redPortal2: Location? = null

    var blueSpawn: Location? = null
    var bluePortal1: Location? = null
    var bluePortal2: Location? = null

    var bluePortal: Cuboid? = null
    var redPortal: Cuboid? = null

    override var arenaType = ArenaType.BRIDGE
        get() = ArenaType.BRIDGE
}