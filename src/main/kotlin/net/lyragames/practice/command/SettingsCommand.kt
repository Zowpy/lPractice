package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.profile.settings.SettingsMenu
import org.bukkit.entity.Player

object SettingsCommand {

    @Command(name = "settings", aliases = ["lpractice:settings"])
    fun settings(@Sender player: Player) {
        SettingsMenu().openMenu(player)
    }
}