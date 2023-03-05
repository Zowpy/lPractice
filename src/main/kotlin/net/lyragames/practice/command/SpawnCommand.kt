package net.lyragames.practice.command

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.entity.Player

object SpawnCommand {

    @Command(value = ["spawn"], description = "go back to spawn")
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