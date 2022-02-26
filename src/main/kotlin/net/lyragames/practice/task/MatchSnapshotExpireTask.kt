package net.lyragames.practice.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.match.snapshot.MatchSnapshot
import org.bukkit.scheduler.BukkitRunnable

object MatchSnapshotExpireTask: BukkitRunnable() {

    init {
        this.runTaskTimerAsynchronously(PracticePlugin.instance, 20 * 60L, 20 * 60L)
    }

    override fun run() {
        MatchSnapshot.snapshots.removeIf { it.isExpired() }
    }
}