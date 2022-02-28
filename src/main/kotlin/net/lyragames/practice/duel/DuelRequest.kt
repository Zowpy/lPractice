package net.lyragames.practice.duel

import mkremins.fanciful.FancyMessage
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/28/2022
 * Project: lPractice
 */

class DuelRequest(var uuid: UUID, var target: UUID, var kit: Kit, var arena: Arena) {

    val executedAt = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60_000
    }

    fun send() {

        val player = Bukkit.getPlayer(target)
        val sender = Bukkit.getPlayer(uuid)

        val profile = Profile.getByUUID(target)
        profile?.duelRequests?.add(this)

        FancyMessage()
            .text("${CC.PINK}${sender.name}${CC.GREEN} (${PlayerUtil.getPing(sender)} ms)${CC.YELLOW} has sent you a duel request with kit ${CC.PINK}${kit.name}${CC.YELLOW} on\n")
            .then()
            .text("${CC.YELLOW}arena ${CC.PINK}${arena.name}${CC.YELLOW}.")
            .then()
            .text("${CC.GREEN} [Click to accept]")
            .command("/duel accept ${sender.name}")
            .send(player)
    }
}