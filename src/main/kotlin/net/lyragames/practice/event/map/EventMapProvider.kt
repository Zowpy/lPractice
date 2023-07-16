package net.lyragames.practice.event.map

import me.zowpy.command.provider.Provider
import me.zowpy.command.provider.exception.CommandExitException
import net.lyragames.practice.manager.EventMapManager

object EventMapProvider: Provider<EventMap> {

    override fun provide(p0: String?): EventMap {
        return p0?.let { EventMapManager.getByName(it) } ?: throw CommandExitException("That specific event map doesn't exist!")
    }

}