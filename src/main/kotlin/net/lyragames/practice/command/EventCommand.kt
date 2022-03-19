package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.impl.SumoEvent
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.manager.EventMapManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object EventCommand {

    @Command(value = ["event host"], description = "host an event")
    fun host(@Sender player: Player) {
        val map = EventMapManager.getFreeMap()

        if (map == null) {
            player.sendMessage("${CC.RED}There are no event maps available!")
            return
        }

        val event = SumoEvent(player.uniqueId, map)
        EventManager.event = event
    }

    @Command(value = ["event join"], description = "join an event")
    fun join(@Sender player: Player) {
        val event = EventManager.event

        if (event == null) {
            player.sendMessage("${CC.RED}There are no active events currently!")
            return
        }

        if (event.requiredPlayers == event.players.size) {
            player.sendMessage("${CC.RED}The event is full!")
            return
        }

        if (event.getPlayer(player.uniqueId) != null) {
            player.sendMessage("${CC.RED}You are already in the event!")
            return
        }

        if (event.state != EventState.ANNOUNCING) {
            player.sendMessage("${CC.RED}The event already started!")
            return
        }

        event.addPlayer(player)
    }
}