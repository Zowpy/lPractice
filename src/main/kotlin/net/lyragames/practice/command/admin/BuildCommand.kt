package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import net.lyragames.llib.utils.CC
import net.lyragames.practice.profile.Profile
import org.bukkit.entity.Player

object BuildCommand {
    @Command(value = ["build"], description = "allow building")
    @Permission("lpractice.command.build")
    fun build(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        if (profile?.match != null) return
            if (profile?.canBuild == true) {
                profile.canBuild = false
                player.sendMessage("${CC.RED}You can no longer build")

            } else {
                profile?.canBuild = true
                player.sendMessage("${CC.GREEN}You may now build")
            }

    }
}