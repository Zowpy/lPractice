package net.lyragames.practice.arena

import me.vaperion.blade.command.argument.BladeProvider
import me.vaperion.blade.command.container.BladeParameter
import me.vaperion.blade.command.context.BladeContext
import me.vaperion.blade.command.exception.BladeExitMessage


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

object ArenaProvider: BladeProvider<Arena> {

    override fun provide(p0: BladeContext, p1: BladeParameter, p2: String?): Arena {
        return p2?.let { Arena.getByName(it) } ?: throw BladeExitMessage("That specific arena doesn't exist!")
    }
}