package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Single
import com.comphenix.protocol.PacketType.Play
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.menu.ArenaRatingMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ArenaRatingCommand: BaseCommand() {

    @CommandPermission("lpractice.command.arenaratings")
    @CommandAlias("arenaratings")
    fun ratings(player: CommandSender, @Single @Name("arena") arena: Arena) {
        ArenaRatingMenu(arena).openMenu(player as Player)
    }
}