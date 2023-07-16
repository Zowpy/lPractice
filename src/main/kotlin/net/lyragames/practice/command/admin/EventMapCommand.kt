package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.map.impl.TNTRunMap
import net.lyragames.practice.event.map.impl.TNTTagMap
import net.lyragames.practice.event.map.type.EventMapType
import net.lyragames.practice.manager.EventMapManager
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object EventMapCommand {

    @Permission("lpractice.command.eventmap.help")
    @Command(name = "eventmap", aliases = ["eventmap help"])
    fun help(@Sender sender: CommandSender) {
        sender.sendMessage("${CC.PRIMARY}EventMap Commands:")
        sender.sendMessage("${CC.SECONDARY}/eventmap create <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap delete <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap spawn <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap pos1 <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap pos2 <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap deadzone <name> <deadzone>")
        sender.sendMessage("${CC.SECONDARY}/eventmap type <name> <type> - you can choose from Sumo & Brackets")
    }

    @Command(name = "eventmap create")
    @Permission("lpractice.command.eventmap.create")
    fun create(@Sender player: Player, name: String) {
        if (EventMapManager.getByName(name) != null) {
            player.sendMessage(CC.RED + "That event map already exists!")
            return
        }

        val arena = EventMap(name)
        arena.save()
        EventMapManager.maps.add(arena)

        player.sendMessage(CC.PRIMARY + "Successfully created " + CC.SECONDARY + "'$name'!")
    }

    @Command(name = "eventmap delete")
    @Permission("lpractice.command.eventmap.delete")
    fun delete(@Sender player: Player, arena: EventMap) {
        arena.delete()
        EventMapManager.maps.remove(arena)

        player.sendMessage(CC.PRIMARY + "Successfully deleted " + CC.SECONDARY + "'${arena.name}'!")
    }

    @Command(name = "eventmap spawn")
    @Permission("lpractice.command.eventmap.setup")
    fun spawn(@Sender player: Player, arena: EventMap) {
        arena.spawn = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " spawn point!")
    }

    @Command(name = "eventmap deadzone")
    @Permission("lpractice.command.eventmap.setup")
    fun deadzone(@Sender player: Player, arena: EventMap, deadzone: Int) {
        if (arena.type != EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }
        (arena as TNTRunMap).deadzone = deadzone
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " deadzone!")
    }

    @Command(name = "eventmap pos1", aliases = ["eventmap position1", "eventmap l1", "eventmap location1"])
    @Permission("lpractice.command.eventmap.setup")
    fun pos1(@Sender player: Player, arena: EventMap) {

        if (arena.type == EventMapType.TNT_TAG || arena.type == EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }

        arena.l1 = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 1!")
    }

    @Command(name = "eventmap pos2", aliases = ["eventmap position2", "eventmap l2", "eventmap location2"])
    @Permission("lpractice.command.eventmap.setup")
    fun pos2(@Sender player: Player, arena: EventMap) {

        if (arena.type == EventMapType.TNT_TAG || arena.type == EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }

        arena.l2 = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 2!")
    }

    @Command(name = "eventmap type")
    @Permission("lpractice.command.eventmap.setup")
    fun type(@Sender player: Player, arena: EventMap, type: EventMapType) {
        arena.type = type

        if (type == EventMapType.TNT_RUN) {
            val newArena = TNTRunMap(arena.name)
            newArena.spawn = arena.spawn

            EventMapManager.maps.removeIf { it.name.equals(arena.name, false) }
            EventMapManager.maps.add(newArena)

            newArena.save()
        }else if (type == EventMapType.TNT_TAG) {
            val newArena = TNTTagMap(arena.name)
            newArena.spawn = arena.spawn

            EventMapManager.maps.removeIf { it.name.equals(arena.name, false) }
            EventMapManager.maps.add(newArena)

            newArena.save()
        }else {
            arena.save()
        }

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s type to ${CC.SECONDARY}${type.eventName}${CC.PRIMARY}!")
    }
}