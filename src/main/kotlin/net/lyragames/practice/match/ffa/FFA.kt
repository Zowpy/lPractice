package net.lyragames.practice.match.ffa

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class FFA(val kit: Kit) {

    val uuid: UUID = UUID.randomUUID()

    val players: MutableList<FFAPlayer> = mutableListOf()

    fun handleDeath(ffaPlayer: FFAPlayer, killer: FFAPlayer) {
        ffaPlayer.death++

        if (killer.kills++ > killer.killStreak) {
            killer.killStreak = killer.kills
        }

        setup(ffaPlayer)
    }

    fun setup(ffaPlayer: FFAPlayer) {
        val player = ffaPlayer.player
        val profile = Profile.getByUUID(player.uniqueId)

        PlayerUtil.reset(player)

        if (Constants.FFA_SPAWN != null) {
            player.teleport(Constants.FFA_SPAWN)
        }

        profile?.getKitStatistic(kit.name)?.generateBooks(player)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun getFFAPlayer(uuid: UUID): FFAPlayer {
        return players.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun sendMessage(message: String) {
        players.stream().map { it.player }
            .forEach { it.sendMessage(CC.translate(message)) }
    }
}