package net.lyragames.practice.arena

import me.zowpy.command.provider.Provider
import me.zowpy.command.provider.exception.CommandExitException

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

object ArenaProvider: Provider<Arena> {

    override fun provide(p0: String): Arena {
        return p0.let { Arena.getByName(it) } ?: throw CommandExitException("That specific arena doesn't exist!")
    }
}