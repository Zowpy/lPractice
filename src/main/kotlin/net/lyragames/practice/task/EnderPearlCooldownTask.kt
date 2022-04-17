package net.lyragames.practice.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.scheduler.BukkitRunnable


object EnderPearlCooldownTask: BukkitRunnable() {

    init {
        this.runTaskTimerAsynchronously(PracticePlugin.instance, 2L, 2L)
    }

    override fun run() {
        for (profile in Profile.profiles) {
            if (profile?.state == ProfileState.MATCH || profile?.state == ProfileState.EVENT || profile?.state == ProfileState.FFA) {
                if (profile.enderPearlCooldown != null && !profile.enderPearlCooldown?.hasExpired()!!) {
                    val player = profile.player

                    val seconds = (profile.enderPearlCooldown?.timeRemaining?.toInt()?.div(1000))

                    if (seconds != null) {
                        player.level = seconds
                    }
                    player.exp = profile.enderPearlCooldown?.timeRemaining?.div(16000.0f) ?: 0f
                }
            }
        }
    }
}