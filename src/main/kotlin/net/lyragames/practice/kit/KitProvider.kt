package net.lyragames.practice.kit

import me.vaperion.blade.command.argument.BladeProvider
import me.vaperion.blade.command.container.BladeParameter
import me.vaperion.blade.command.context.BladeContext
import me.vaperion.blade.command.exception.BladeExitMessage

object KitProvider: BladeProvider<Kit> {

    override fun provide(p0: BladeContext, p1: BladeParameter, p2: String?): Kit? {
        return p2?.let { Kit.getByName(it) } ?: throw BladeExitMessage("That kit doesn't exist!")
    }
}