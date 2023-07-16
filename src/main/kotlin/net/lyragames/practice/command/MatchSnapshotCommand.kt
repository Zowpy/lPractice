package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.match.menu.MatchDetailsMenu
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player
import java.util.*


object MatchSnapshotCommand {

    @Command(name = "matchsnapshot")
    fun command(@Sender player: Player, id: String) {

        val cachedInventory: MatchSnapshot? = try {
            MatchSnapshot.getByUuid(UUID.fromString(id))
        } catch (e: Exception) {
            MatchSnapshot.getByName(id)
        }

        if (cachedInventory == null) {
            player.sendMessage("${CC.RED}Couldn't find the requested inventory.")
            return
        }

        MatchDetailsMenu(cachedInventory).openMenu(player)
    }
}