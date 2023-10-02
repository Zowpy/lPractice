package net.lyragames.practice.kit

import me.zowpy.command.provider.Provider
import me.zowpy.command.provider.exception.CommandExitException
import net.lyragames.practice.Locale

object KitProvider: Provider<Kit> {

    override fun provide(p0: String): Kit {
        return p0.let { Kit.getByName(it) } ?: throw CommandExitException(Locale.CANT_FIND_KIT.getMessage())
    }
}