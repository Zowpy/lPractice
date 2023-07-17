package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.utils.CC
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object TeleportCommand {


    @Command(name = "tppos")
    @Permission("lpractice.command.set.spawn")
    fun teleport(@Sender player: Player, x:Double, y:Double, z: Double) {

        val world = Bukkit.getServer().getWorld(player.world.uid)
        val location = Location(world, x, y, z)
        player.teleport(location)



        player.sendMessage("${CC.GREEN}Successfully teleported to ${x}, ${y}, ${z}")
    }
}