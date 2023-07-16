package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

object BuildCommand {

    @Command(name = "build")
    @Permission("lpractice.command.build")
    fun build(@Sender player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) return

        if (profile?.canBuild!!) {
            profile.canBuild = false
            player.sendMessage("${CC.RED}You can no longer build")
        } else {
            profile.canBuild = true
            player.sendMessage("${CC.GREEN}You may now build")
        }
    }
}