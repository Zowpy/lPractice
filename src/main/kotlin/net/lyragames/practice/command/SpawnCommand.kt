package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(name = "spawn")
    fun spawn(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state != ProfileState.LOBBY || profile.state != ProfileState.QUEUE) {
            player.sendMessage("${CC.RED}You cannot do this now!")
            return
        }

        Hotbar.giveHotbar(profile)
        player.teleport(Constants.SPAWN)
    }
}