package net.lyragames.practice.arena.impl.fireball

import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.type.ArenaType

class FireBallFightArena(name: String): BedWarsArena(name) {

    override var arenaType = ArenaType.FIREBALL_FIGHT
}