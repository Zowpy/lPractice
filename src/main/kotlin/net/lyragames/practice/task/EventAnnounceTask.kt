package net.lyragames.practice.task

import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.practice.events.EventState
import net.lyragames.practice.events.EventType
import net.lyragames.practice.manager.EventManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object EventAnnounceTask: BukkitRunnable() {

    override fun run() {
        val event = EventManager.event ?: return
        if (event.state != EventState.ANNOUNCING) return

        val host = Bukkit.getPlayer(event.host)
        val fancyMessage = buildMessage(host, event.type)

        for (player in Bukkit.getOnlinePlayers()) {
            if (player.uniqueId == host.uniqueId) continue

            fancyMessage.send(player)
        }
    }

    private fun buildMessage(host: Player, eventType: EventType) : FancyMessage {
        return FancyMessage()
            .text("${CC.GREEN}${host.name}${CC.YELLOW} is hosting ${CC.GREEN}${eventType.eventName}${CC.YELLOW} event!")
            .then()
            .text("${CC.GREEN}[Click to join]")
            .command("/event join")
    }
}