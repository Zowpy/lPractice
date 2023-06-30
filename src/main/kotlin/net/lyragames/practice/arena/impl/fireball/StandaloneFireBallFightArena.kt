package net.lyragames.practice.arena.impl.fireball

import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.type.ArenaType

class StandaloneFireBallFightArena(name: String): StandaloneBedWarsArena(name) {

    override var arenaType = ArenaType.FIREBALL_FIGHT
}