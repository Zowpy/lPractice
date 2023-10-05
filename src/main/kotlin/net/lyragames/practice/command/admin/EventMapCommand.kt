package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import net.lyragames.practice.event.map.EventMap
import net.lyragames.practice.event.map.impl.TNTRunMap
import net.lyragames.practice.event.map.impl.TNTTagMap
import net.lyragames.practice.event.map.type.EventMapType
import net.lyragames.practice.manager.EventMapManager
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
@CommandAlias("eventmap")
@CommandPermission("lpractice.command.eventmap")

object EventMapCommand: BaseCommand() {

   @HelpCommand()
   @Syntax("[page]")
    fun help(help: CommandHelp) {
        help.showHelp()
       /*
        sender.sendMessage("${CC.PRIMARY}EventMap Commands:")
        sender.sendMessage("${CC.SECONDARY}/eventmap create <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap delete <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap spawn <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap pos1 <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap pos2 <name>")
        sender.sendMessage("${CC.SECONDARY}/eventmap deadzone <name> <deadzone>")
        sender.sendMessage("${CC.SECONDARY}/eventmap type <name> <type> - you can choose from Sumo & Brackets")

        */
    }

    @Subcommand("create")

    fun create(player: CommandSender, @Single @Name("map") name: String) {
        if (EventMapManager.getByName(name) != null) {
            player.sendMessage(CC.RED + "That event map already exists!")
            return
        }

        val arena = EventMap(name)
        arena.save()
        EventMapManager.maps.add(arena)

        player.sendMessage(CC.PRIMARY + "Successfully created " + CC.SECONDARY + "'$name'!")
    }

    @Subcommand("delete")
    fun delete(player: CommandSender, @Single @Name("map") arena: EventMap) {
        arena.delete()
        EventMapManager.maps.remove(arena)

        player.sendMessage(CC.PRIMARY + "Successfully deleted " + CC.SECONDARY + "'${arena.name}'!")
    }

    @Subcommand("spawn")
    fun spawn(player: CommandSender, @Single @Name("map") arena: EventMap) {
        arena.spawn = (player as Player).location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " spawn point!")
    }

    @Subcommand("deadzone")
    fun deadzone(player: CommandSender, @Single @Name("map") arena: EventMap, @Single @Name("deadzone")deadzone: Int) {
        if (arena.type != EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }
        (arena as TNTRunMap).deadzone = deadzone
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " deadzone!")
    }

    @Subcommand("pos1|position1|l1|location1")
    fun pos1(player: CommandSender, @Single @Name("map") arena: EventMap) {

        if (arena.type == EventMapType.TNT_TAG || arena.type == EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }

        arena.l1 = (player as Player).location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 1!")
    }

    @Subcommand("pos2|position2|l2|location2")
    fun pos2(player: CommandSender, @Single @Name("map") arena: EventMap) {

        if (arena.type == EventMapType.TNT_TAG || arena.type == EventMapType.TNT_RUN) {
            player.sendMessage("${CC.RED}That option is not supported for this map type!")
            return
        }

        arena.l2 = (player as Player).location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 2!")
    }

    @Subcommand("eventmap type")
    fun type(player: CommandSender, @Single @Name("map") arena: EventMap, @Single @Name("type") type: EventMapType) {
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