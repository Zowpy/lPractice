package net.lyragames.practice.command.admin

import me.vaperion.blade.command.annotation.Command
import me.vaperion.blade.command.annotation.Permission
import me.vaperion.blade.command.annotation.Sender
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cuboid
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.impl.mlgrush.MLGRushArena
import net.lyragames.practice.arena.impl.mlgrush.StandaloneMLGRushArena
import net.lyragames.practice.arena.menu.ArenaManageMenu
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.kit.admin.AdminKitEditMenu
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
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/arena create <name>")
        player.sendMessage("${CC.SECONDARY}/arena delete <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos1 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos2 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena min <arena>")
        player.sendMessage("${CC.SECONDARY}/arena max <arena>")
        player.sendMessage("${CC.SECONDARY}/arena deadzone <arena> ${CC.GRAY}- set an arena's lowest Y location (Used for bridges, bedfight, etc)")
        player.sendMessage("${CC.SECONDARY}/arena bed1 <arena> ${CC.GRAY}- only supported for mlgrush (stand on bed)")
        player.sendMessage("${CC.SECONDARY}/arena bed2 <arena> ${CC.GRAY}- only supported for mlgrush (stand on bed)")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.PRIMARY}BedWars Arena Command:")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/arena redspawn <arena>")
        player.sendMessage("${CC.SECONDARY}/arena bluespawn <arena>")
        player.sendMessage("${CC.SECONDARY}/arena redbed <arena>")
        player.sendMessage("${CC.SECONDARY}/arena bluebed <arena>")
        player.sendMessage(CC.translate("&7&m---------------------"))
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

        player.sendMessage("${CC.PRIMARY}Successfully created ${CC.SECONDARY}$name${CC.PRIMARY}!")
    }

    @Command(value = ["arena delete"], description = "delete an arena")
    @Permission("lpractice.command.arena.delete")
    fun delete(@Sender player: Player, arena: Arena) {
        arena.delete()
        Arena.arenas.remove(arena)

        player.sendMessage("${CC.PRIMARY}Successfully deleted ${CC.SECONDARY}${arena.name}${CC.PRIMARY}!")
    }

    @Command(value = ["arena pos1", "arena position1", "arena l1", "arena location1"], description = "set an arena's first location")
    @Permission("lpractice.command.arena.setup")
    fun pos1(@Sender player: Player, arena: Arena) {
        arena.l1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 1!")
    }

    @Command(value = ["arena pos2", "arena position2", "arena l2", "arena location2"], description = "set an arena's second location")
    @Permission("lpractice.command.arena.setup")
    fun pos2(@Sender player: Player, arena: Arena) {
        arena.l2 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 2!")
    }

    @Command(value = ["arena min", "arena minimum"], description = "set an arena's min location")
    @Permission("lpractice.command.arena.setup")
    fun min(@Sender player: Player, arena: Arena) {
        arena.min = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s min location!")

        if (arena.max != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }

    @Command(value = ["arena max", "arena maximum"], description = "set an arena's max location")
    @Permission("lpractice.command.arena.setup")
    fun max(@Sender player: Player, arena: Arena) {
        arena.max = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s max location!")

        if (arena.min != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }


    @Command(value = ["arena deadzone", "arena yval"], description = "set an arena's lowest Y location (Used for sumo, bridges, bedfight, pearlfight, etc")
    @Permission("lpractice.command.arena.setup")
    fun deadzone(@Sender player: Player, arena: Arena) {
        arena.deadzone = player.location.y.toInt()
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s deadzone!")
    }

    @Command(value = ["arena type"], description = "change an arena type")
    fun type(@Sender player: Player, arena: Arena, type: ArenaType) {
        if (type == ArenaType.MLGRUSH) {
            val newArena = StandaloneMLGRushArena(arena.name)
            newArena.l1 = arena.l1
            newArena.l2 = arena.l2
            newArena.deadzone = arena.deadzone
            newArena.min = arena.min
            newArena.max = arena.max
            newArena.arenaType = type

            if (arena is StandaloneArena) {
                for (duplicate in arena.duplicates) {
                    val newDuplicate = MLGRushArena(duplicate.name)
                    newDuplicate.l1 = duplicate.l1
                    newDuplicate.l2 = duplicate.l2
                    newDuplicate.min = duplicate.min
                    newDuplicate.max = duplicate.max
                    newDuplicate.bed1 = (duplicate as MLGRushArena).bed1
                    newDuplicate.bed2 = duplicate.bed2
                    newDuplicate.deadzone = duplicate.deadzone
                    newDuplicate.duplicate = true

                    newDuplicate.bounds = Cuboid(newDuplicate.min, newDuplicate.max)
                    newDuplicate.arenaType = ArenaType.BEDFIGHT

                    arena.duplicates.removeIf { it.name.equals(duplicate.name, false) }
                    arena.duplicates.add(newDuplicate)
                }
            }

            Arena.arenas.removeIf { it.name.equals(arena.name, false) }
            Arena.arenas.add(newArena)

            newArena.save()
        }else if (type == ArenaType.BEDFIGHT) {
            val newArena = StandaloneBedWarsArena(arena.name)
            newArena.l1 = arena.l1
            newArena.l2 = arena.l2
            newArena.deadzone = arena.deadzone
            newArena.min = arena.min
            newArena.max = arena.max
            newArena.arenaType = type

            if (arena is StandaloneArena) {
                for (duplicate in arena.duplicates) {
                    val newDuplicate = BedWarsArena(duplicate.name)
                    newDuplicate.min = duplicate.min
                    newDuplicate.max = duplicate.max
                    newDuplicate.redBed = (duplicate as BedWarsArena).redBed
                    newDuplicate.blueBed = duplicate.blueBed
                    newDuplicate.blueSpawn = duplicate.blueSpawn
                    newDuplicate.redSpawn = duplicate.redSpawn
                    newDuplicate.deadzone = duplicate.deadzone
                    newDuplicate.duplicate = true

                    newDuplicate.bounds = Cuboid(newDuplicate.min, newDuplicate.max)
                    newDuplicate.arenaType = ArenaType.BEDFIGHT

                    arena.duplicates.removeIf { it.name.equals(duplicate.name, false) }
                    arena.duplicates.add(newDuplicate)
                }
            }

            Arena.arenas.removeIf { it.name.equals(arena.name, false) }
            Arena.arenas.add(newArena)

            newArena.save()
        }

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s type to ${CC.SECONDARY}${type.name}${CC.PRIMARY}!")
    }

    @Command(value = ["arena bed1", "arena b1"], description = "set an arena's first bed location")
    @Permission("lpractice.command.arena.setup")
    fun bed1(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.MLGRUSH) {
            player.sendMessage("${CC.RED}This command is only supported for MLGRush arenas!")
            return
        }
        (arena as StandaloneMLGRushArena).bed1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s bed location 1!")
    }

    @Command(value = ["arena bed2", "arena b2"], description = "set an arena's second bed location")
    @Permission("lpractice.command.arena.setup")
    fun bed2(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.MLGRUSH) {
            player.sendMessage("${CC.RED}This command is only supported for MLGRush arenas!")
            return
        }
        (arena as StandaloneMLGRushArena).bed2 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s bed location 2!")
    }

    @Command(value = ["arena redBed", "arena rb"], description = "set an arena's red bed")
    @Permission("lpractice.command.arena.setup")
    fun redBed(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights arenas!")
            return
        }
        (arena as StandaloneBedWarsArena).redBed = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red bed location!")
    }

    @Command(value = ["arena blueBed", "arena bb"], description = "set an arena's blue bed")
    @Permission("lpractice.command.arena.setup")
    fun blueBed(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights arenas!")
            return
        }
        (arena as StandaloneBedWarsArena).blueBed = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue bed location!")
    }

    @Command(value = ["arena redspawn"], description = "set an arena's red location")
    @Permission("lpractice.command.arena.setup")
    fun redPos(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).redSpawn = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red location!")
    }

    @Command(value = ["arena bluespawn"], description = "set an arena's second location")
    @Permission("lpractice.command.arena.setup")
    fun bluePos(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).blueSpawn = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue location!")
    }

    @Command(value = ["arena menu"], description = "manage active arenas")
    @Permission("lpractice.command.arena.menu")
    fun manage(@Sender player: Player, arena: Arena) {
        ArenaManageMenu(arena).openMenu(player)

    }
}