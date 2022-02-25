package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.match.menu.MatchDetailsMenu
import net.lyragames.practice.match.snapshot.MatchSnapshot
import org.bukkit.entity.Player
import java.util.*


object MatchSnapshotCommand {

    @Command(value = ["matchsnapshot"], description = "view a match snapshot")
    fun command(@Sender player: Player, id: String) {

        val cachedInventory: MatchSnapshot? = try {
            MatchSnapshot.getByUuid(UUID.fromString(id))
        } catch (e: Exception) {
            MatchSnapshot.getByName(id)
        }

        if (cachedInventory == null) {
            player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.")
            return
        }

        MatchDetailsMenu(cachedInventory).openMenu(player)
    }
}