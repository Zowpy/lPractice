package net.lyragames.practice.task

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.event.impl.TNTTagEvent
import net.lyragames.practice.manager.EventManager
import org.bukkit.scheduler.BukkitRunnable


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 4/1/2022
 * Project: lPractice
 */

object TNTTagTask: BukkitRunnable() {

    init {
        this.runTaskTimerAsynchronously(PracticePlugin.instance, 20 * 60L, 20 * 60L)
    }

    override fun run() {
        val event = EventManager.event ?: return
        if (event.type != EventType.TNT_TAG) return
        if (event.state != EventState.FIGHTING) return

        (event as TNTTagEvent).endRound(null)
    }
}