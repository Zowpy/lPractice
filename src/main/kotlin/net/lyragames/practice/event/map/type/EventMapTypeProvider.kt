package net.lyragames.practice.event.map.type

import me.zowpy.command.provider.Provider
import me.zowpy.command.provider.exception.CommandExitException

object EventMapTypeProvider: Provider<EventMapType> {

    override fun provide(p0: String): EventMapType {
        if (EventMapType.valueOf(p0!!) == null) throw CommandExitException("Invalid event map type!") else return EventMapType.valueOf(p0)
    }
}