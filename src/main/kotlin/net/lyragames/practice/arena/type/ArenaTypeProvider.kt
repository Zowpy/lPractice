package net.lyragames.practice.arena.type

import me.vaperion.blade.command.argument.BladeProvider
import me.vaperion.blade.command.container.BladeParameter
import me.vaperion.blade.command.context.BladeContext
import me.vaperion.blade.command.exception.BladeExitMessage

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/28/2022
 * Project: lPractice
 */

object ArenaTypeProvider: BladeProvider<ArenaType> {

    override fun provide(p0: BladeContext, p1: BladeParameter, p2: String?): ArenaType? {
        return ArenaType.valueOf(p2!!.uppercase()) ?: throw BladeExitMessage("Invalid arena type!")
    }

    override fun suggest(context: BladeContext, input: String): MutableList<String> {
        return ArenaType.values().map { it.name }
            .filter { it.startsWith(input) }
            .toMutableList()
    }
}