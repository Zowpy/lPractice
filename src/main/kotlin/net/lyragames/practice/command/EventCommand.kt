package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.Locale
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.menu.EventHostMenu
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EventCommand {

    @Command(name = "event")
    fun help(@Sender player: CommandSender) {
        player.sendMessage("${CC.SECONDARY}Event Help")
        player.sendMessage(CC.CHAT_BAR)
        player.sendMessage("${CC.PRIMARY}/event join")
        player.sendMessage("${CC.PRIMARY}/event host")
        player.sendMessage("${CC.PRIMARY}/event start")
        player.sendMessage(CC.CHAT_BAR)
    }

    @Command(name = "event host")
    fun host(@Sender player: Player) {
        EventHostMenu().openMenu(player)
    }

    @Command(name = "event join")
    fun join(@Sender player: Player) {
        val event = EventManager.event

        if (event == null) {
            player.sendMessage(Locale.NO_ACTIVE_EVENTS.getMessage())
            return
        }

        if (event.requiredPlayers == event.players.size) {
            player.sendMessage(Locale.EVENT_FULL.getMessage())
            return
        }

        if (event.getPlayer(player.uniqueId) != null) {
            player.sendMessage(Locale.ALREADY_IN_EVENT.getMessage())
            return
        }

        if (event.state != EventState.ANNOUNCING) {
            player.sendMessage(Locale.ALREADY_STARTED.getMessage())
            return
        }

        event.addPlayer(player)
    }

    @Permission("lpractice.command.event.forcestart")
    @Command(name = "event start", aliases = ["event forcestart", "event fs"])
    fun forcestart(@Sender player: Player) {

        if (EventManager.event?.players?.size!! < 2) {
            player.sendMessage(Locale.NOT_ENOUGH_PLAYER.getMessage())
            return
        }

        EventManager.event?.startRound()
    }
}