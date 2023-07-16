package net.lyragames.practice.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.scheduler.BukkitRunnable

object ArrowCooldownTask: BukkitRunnable() {

    init {
        this.runTaskTimerAsynchronously(PracticePlugin.instance, 2L, 2L)
    }

    override fun run() {
        for (profile in Profile.profiles) {
            if (profile?.state == ProfileState.MATCH && profile.match != null) {

                val match = Match.getByUUID(profile.match!!)

                if (match == null) continue

                if (match.kit.kitData.bridge) {
                    if (profile.arrowCooldown != null && !profile.arrowCooldown?.hasExpired()!!) {
                        val player = profile.player

                        val seconds = (profile.arrowCooldown?.timeRemaining?.toInt()?.div(1000))

                        if (seconds != null) {
                            player.level = seconds
                        }

                        player.exp = profile.arrowCooldown?.timeRemaining?.div(6000.0f) ?: 0f
                    }
                }
            }
        }
    }
}