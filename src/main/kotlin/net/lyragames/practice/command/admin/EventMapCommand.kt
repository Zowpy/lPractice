package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.manager.EventMapManager
import org.bukkit.entity.Player

object EventMapCommand {

    @Command(value = ["eventmap create"], description = "create a new event map")
    @Permission("lpractice.command.eventmap.create")
    fun create(@Sender player: Player, name: String) {
        if (EventMapManager.getByName(name) != null) {
            player.sendMessage(CC.RED + "That event map already exists!")
            return
        }

        val arena = EventMap(name)
        arena.save()
        EventMapManager.maps.add(arena)

        player.sendMessage(CC.YELLOW + "Successfully created " + CC.GOLD + "'$name'!")
    }

    @Command(value = ["eventmap delete"], description = "delete an event map")
    @Permission("lpractice.command.eventmap.delete")
    fun delete(@Sender player: Player, arena: EventMap) {
        arena.delete()
        EventMapManager.maps.remove(arena)

        player.sendMessage(CC.YELLOW + "Successfully deleted " + CC.GOLD + "'${arena.name}'!")
    }

    @Command(value = ["eventmap pos1", "eventmap position1", "eventmap l1", "eventmap location1"], description = "set an event map's first location")
    @Permission("lpractice.command.eventmap.setup")
    fun pos1(@Sender player: Player, arena: EventMap) {
        arena.l1 = player.location
        arena.save()

        player.sendMessage(CC.YELLOW + "Successfully set " + CC.GOLD + arena.name + CC.YELLOW + " location 1!")
    }

    @Command(value = ["eventmap pos2", "eventmap position2", "eventmap l2", "eventmap location2"], description = "set an event map's second location")
    @Permission("lpractice.command.eventmap.setup")
    fun pos2(@Sender player: Player, arena: Arena) {
        arena.l2 = player.location
        arena.save()

        player.sendMessage(CC.YELLOW + "Successfully set " + CC.GOLD + arena.name + CC.YELLOW + " location 2!")
    }
}