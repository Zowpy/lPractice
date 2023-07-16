package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.menu.ArenaRatingMenu
import org.bukkit.entity.Player

object ArenaRatingCommand {

    @Permission("lpractice.command.arenaratings")
    @Command(name = "arenaratings")
    fun ratings(@Sender player: Player, arena: Arena) {
        ArenaRatingMenu(arena).openMenu(player)
    }
}