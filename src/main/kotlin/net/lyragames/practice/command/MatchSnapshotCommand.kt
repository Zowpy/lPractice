package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import net.lyragames.practice.Locale
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.match.menu.MatchDetailsMenu
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.utils.CC
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object MatchSnapshotCommand: BaseCommand() {

    @CommandAlias("matchsnapshot")
    fun command(player: CommandSender, id: String) {

        val cachedInventory: MatchSnapshot? = try {
            MatchSnapshot.getByUuid(UUID.fromString(id))
        } catch (e: Exception) {
            MatchSnapshot.getByName(id)
        }

        if (cachedInventory == null) {
            player.sendMessage(Locale.COULDNT_FIND_INVENTORY.getMessage())
            return
        }

        Bukkit.getScheduler().runTask(PracticePlugin.instance) { MatchDetailsMenu(cachedInventory).openMenu(player as Player) }

    }
}