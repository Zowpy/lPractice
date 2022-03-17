package net.lyragames.practice.event.map

import me.vaperion.blade.command.argument.BladeProvider
import me.vaperion.blade.command.container.BladeParameter
import me.vaperion.blade.command.context.BladeContext
import me.vaperion.blade.command.exception.BladeExitMessage
import net.lyragames.practice.manager.EventMapManager

object EventMapProvider: BladeProvider<EventMap> {

    override fun provide(p0: BladeContext, p1: BladeParameter, p2: String?): EventMap {
        return p2?.let { EventMapManager.getByName(it) } ?: throw BladeExitMessage("That specific event map doesn't exist!")
    }

}