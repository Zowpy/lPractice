package net.lyragames.practice.duel

import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.PlayerUtil
import net.lyragames.practice.utils.TextBuilder
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

    var executedAt = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60_000
    }

    fun send() {

        val player = Bukkit.getPlayer(target)
        val sender = Bukkit.getPlayer(uuid)

        val profile = Profile.getByUUID(target)
        profile?.duelRequests?.add(this)

        val message = TextBuilder()
            .setText("${CC.SECONDARY}${sender.name}${CC.GREEN} (${PlayerUtil.getPing(sender)} ms)${CC.PRIMARY} has sent you a duel request with kit ${CC.SECONDARY}${kit.name}${CC.PRIMARY} on")
            .then()
            .setText("${CC.PRIMARY} arena ${CC.SECONDARY}${arena.name}${CC.PRIMARY}.")
            .then()
            .setText("${CC.SECONDARY} [Click to accept]")
            .setCommand("/duel accept ${sender.name}")
            .then()
            .build()

        player.spigot().sendMessage(message)
    }
}