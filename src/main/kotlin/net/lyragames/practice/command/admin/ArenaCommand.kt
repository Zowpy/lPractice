package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cuboid
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena
import org.bukkit.ChatColor
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

object ArenaCommand {

    @Permission("lpractice.command.arena.help")
    @Command(value = ["arena", "arena help"], description = "arena help message")
    fun help(@Sender player: Player) {
        player.sendMessage("${CC.PRIMARY}Arena Commands:")
        player.sendMessage("${CC.SECONDARY}/arena create <name>")
        player.sendMessage("${CC.SECONDARY}/arena delete <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos1 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos2 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena min <arena>")
        player.sendMessage("${CC.SECONDARY}/arena max <arena>")
    }

    @Command(value = ["arena create"], description = "create a new arena")
    @Permission("lpractice.command.arena.create")
    fun create(@Sender player: Player, name: String) {
        if (Arena.getByName(name) != null) {
            player.sendMessage(CC.RED + "That arena already exists!")
            return
        }

        val arena = StandaloneArena(name)
        arena.save()
        Arena.arenas.add(arena)

        player.sendMessage(CC.PRIMARY + "Successfully created " + CC.SECONDARY + "'$name'!")
    }

    @Command(value = ["arena delete"], description = "delete an arena")
    @Permission("lpractice.command.arena.delete")
    fun delete(@Sender player: Player, arena: Arena) {
        arena.delete()
        Arena.arenas.remove(arena)

        player.sendMessage(CC.PRIMARY + "Successfully deleted ${CC.SECONDARY}'${arena.name}'!")
    }

    @Command(value = ["arena pos1", "arena position1", "arena l1", "arena location1"], description = "set an arena's first location")
    @Permission("lpractice.command.arena.setup")
    fun pos1(@Sender player: Player, arena: Arena) {
        arena.l1 = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 1!")
    }

    @Command(value = ["arena pos2", "arena position2", "arena l2", "arena location2"], description = "set an arena's second location")
    @Permission("lpractice.command.arena.setup")
    fun pos2(@Sender player: Player, arena: Arena) {
        arena.l2 = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " location 2!")
    }

    @Command(value = ["arena min", "arena minimum"], description = "set an arena's min location")
    @Permission("lpractice.command.arena.setup")
    fun min(@Sender player: Player, arena: Arena) {
        arena.min = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " min location!")

        if (arena.max != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }

    @Command(value = ["arena max", "arena maximum"], description = "set an arena's max location")
    @Permission("lpractice.command.arena.setup")
    fun max(@Sender player: Player, arena: Arena) {
        arena.max = player.location
        arena.save()

        player.sendMessage(CC.PRIMARY + "Successfully set " + CC.SECONDARY + arena.name + CC.PRIMARY + " max location!")

        if (arena.min != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }


 /*   @Command(value = ["arena deadzone", "arena yval"], description = "set an arena's lowest Y location (Used for sumo, bridges, bedfight, pearlfight, etc")
    @Permission("lpractice.command.arena.setup")
    fun deadzone(@Sender player: Player, arena: Arena) {


       // arena.deadzone = player.location.y
        arena.save()

        player.sendMessage("${CC.YELLOW}Successfully set${CC.GOLD}" + arena.name + "${CC.YELLOW}'s deadzone location!")

    }*/
}