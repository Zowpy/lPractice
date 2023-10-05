package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.Subcommand

import net.lyragames.practice.profile.settings.SettingsMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object SettingsCommand: BaseCommand() {

    @Subcommand("settings|lpractice:settings")
    fun settings(player: CommandSender) {
        SettingsMenu().openMenu(player as Player)
    }
}