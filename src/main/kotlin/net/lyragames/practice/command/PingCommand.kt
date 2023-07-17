package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Named
import me.zowpy.command.annotation.Optional
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.utils.CC
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

object PingCommand {

    @Permission("lpractice.command.ping")
    @Command(name = "ping")
    fun ping(@Sender player: Player, @Named("target") @Optional target: Player?) {


        var craftPlayer = player as CraftPlayer

        if (target != null) {
            craftPlayer = target as CraftPlayer
        }


        val ping = craftPlayer.handle.ping

        player.sendMessage(if (target == null)
            "${CC.PRIMARY}Your ping is ${CC.SECONDARY}$ping ${CC.PRIMARY}ms." else
        "${CC.SECONDARY}${target.name}${CC.PRIMARY} ping is ${CC.SECONDARY}$ping ${CC.PRIMARY}ms.")
    }
}