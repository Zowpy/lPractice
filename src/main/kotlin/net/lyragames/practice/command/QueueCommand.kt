package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.practice.queue.menu.QueueMenu
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

class QueueCommand {

    @Command(value = ["queue", "q"], description = "this is a temporary command")
    fun queue(@Sender player: Player) {
        QueueMenu(false).openMenu(player)
    }
}