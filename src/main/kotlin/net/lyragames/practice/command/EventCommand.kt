package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.menu.EventHostMenu
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object EventCommand {

    @Command(name = "event host")
    fun host(@Sender player: Player) {
        EventHostMenu().openMenu(player)
    }

    @Command(name = "event join")
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

    @Permission("lpractice.command.event.forcestart")
    @Command(name = "event start", aliases = ["event forcestart", "event fs"])
    fun forcestart(@Sender player: Player) {

        if (EventManager.event?.players?.size!! < 2) {
            player.sendMessage("${CC.RED}You need at least 2 players to force start!")
            return
        }

        EventManager.event?.startRound()
    }
}