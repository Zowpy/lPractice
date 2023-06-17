package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.practice.profile.settings.SettingsMenu
import org.bukkit.entity.Player

object SettingsCommand {

    @Command(value = ["settings", "lpractice:settings"], description = "change your settings")
    fun settings(@Sender player: Player) {
        SettingsMenu().openMenu(player)
    }
}