package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.menu.ArenaRatingMenu
import org.bukkit.entity.Player

object ArenaRatingCommand {

    @Permission("lpractice.command.arenaratings")
    @Command("arenaratings")
    fun ratings(@Sender player: Player, arena: Arena) {
        ArenaRatingMenu(arena).openMenu(player)
    }
}