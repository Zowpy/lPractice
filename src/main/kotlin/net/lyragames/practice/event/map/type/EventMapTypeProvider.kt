package net.lyragames.practice.event.map.type

import me.vaperion.blade.command.argument.BladeProvider
import me.vaperion.blade.command.container.BladeParameter
import me.vaperion.blade.command.context.BladeContext
import me.vaperion.blade.command.exception.BladeExitMessage

object EventMapTypeProvider: BladeProvider<EventMapType> {

    override fun provide(p0: BladeContext, p1: BladeParameter, p2: String?): EventMapType {
        if (EventMapType.valueOf(p2!!) == null) throw BladeExitMessage("Invalid event map type!") else return EventMapType.valueOf(p2)
    }

    override fun suggest(context: BladeContext, input: String): MutableList<String> {
        return EventMapType.values().map { it.name }
            .filter { it.startsWith(input) }
            .toMutableList()
    }
}