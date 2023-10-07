package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import net.lyragames.practice.Locale
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.menu.EventHostMenu
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
@CommandAlias("event")
object EventCommand: BaseCommand() {
    @HelpCommand
    fun help(player: CommandSender) {
        player.sendMessage("${CC.SECONDARY}Event Help")
        player.sendMessage(CC.CHAT_BAR)
        player.sendMessage("${CC.PRIMARY}/event join")
        player.sendMessage("${CC.PRIMARY}/event host")
        player.sendMessage("${CC.PRIMARY}/event start")
        player.sendMessage(CC.CHAT_BAR)
    }

    @Subcommand("host")
    fun host(player: CommandSender) {
        EventHostMenu().openMenu(player as Player)
    }

    @Subcommand("join")
    fun join(player: CommandSender) {
        val event = EventManager.event

        if (event == null) {
            player.sendMessage(Locale.NO_ACTIVE_EVENTS.getMessage())
            return
        }

        if (event.requiredPlayers == event.players.size) {
            player.sendMessage(Locale.EVENT_FULL.getMessage())
            return
        }

        if (event.getPlayer((player as Player).uniqueId) != null) {
            player.sendMessage(Locale.ALREADY_IN_EVENT.getMessage())
            return
        }

        if (event.state != EventState.ANNOUNCING) {
            player.sendMessage(Locale.ALREADY_STARTED.getMessage())
            return
        }

        event.addPlayer(player)
    }

    @CommandPermission("lpractice.command.event.forcestart")
    @Subcommand("start|forcestart|fs")
    fun forcestart(player: CommandSender) {

        if (EventManager.event?.players?.size!! < 2) {
            player.sendMessage(Locale.NOT_ENOUGH_PLAYER.getMessage())
            return
        }

        EventManager.event?.startRound()
    }
}