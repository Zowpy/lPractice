package net.lyragames.practice.arena.type

import me.zowpy.command.provider.Provider
import me.zowpy.command.provider.exception.CommandExitException

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/28/2022
 * Project: lPractice
 */

object ArenaTypeProvider: Provider<ArenaType> {

    override fun provide(p0: String?): ArenaType {
        return ArenaType.valueOf(p0!!.uppercase()) ?: throw CommandExitException("Invalid arena type!")
    }
}