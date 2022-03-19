package net.lyragames.practice.task

import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object EventAnnounceTask: BukkitRunnable() {

    init {
        this.runTaskTimer(PracticePlugin.instance, 20 * 4L, 20 * 4L)
    }

    override fun run() {
        val event = EventManager.event ?: return
        if (event.state != EventState.ANNOUNCING) return

        if (event.requiredPlayers == event.players.size) {
            event.startRound()
            return
        }

        if (System.currentTimeMillis() - event.created >= (1000 * 60) * 5) {
            Bukkit.broadcastMessage("${CC.RED}Stopped event as no one joined!")
            event.players.stream().forEach {
                Profile.getByUUID(it.uuid)?.state = ProfileState.LOBBY
            }
            event.players.clear()

            EventManager.event = null
            return
        }

        val host = Bukkit.getPlayer(event.host)
        val fancyMessage = buildMessage(host, event.type)

        for (player in Bukkit.getOnlinePlayers()) {
         //   if (player.uniqueId == host.uniqueId) continue
             if (event.getPlayer(player.uniqueId) != null) continue

            fancyMessage.send(player)
        }
    }

    private fun buildMessage(host: Player, eventType: EventType) : FancyMessage {
        return FancyMessage()
            .text("${CC.GREEN}${host.name}${CC.YELLOW} is hosting ${CC.GREEN}${eventType.eventName}${CC.YELLOW} event!")
            .then()
            .text("${CC.GREEN} [Click to join]")
            .command("/event join")
    }
}