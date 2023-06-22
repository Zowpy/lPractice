package net.lyragames.practice.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.manager.FFAManager
import org.bukkit.scheduler.BukkitRunnable

object FFAItemClearTask: BukkitRunnable() {

    init {
        this.runTaskTimer(PracticePlugin.instance, 20 * 60, 20 * 60)
    }

    override fun run() {
        for (ffa in FFAManager.ffaMatches) {
            ffa.droppedItems.forEach { it.remove() }
            ffa.droppedItems.clear()
        }
    }
}