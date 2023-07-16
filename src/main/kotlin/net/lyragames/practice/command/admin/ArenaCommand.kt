package net.lyragames.practice.command.admin

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Permission
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.arena.impl.bedwars.BedWarsArena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.arena.impl.bridge.BridgeArena
import net.lyragames.practice.arena.impl.bridge.StandaloneBridgeArena
import net.lyragames.practice.arena.impl.fireball.StandaloneFireBallFightArena
import net.lyragames.practice.arena.impl.mlgrush.MLGRushArena
import net.lyragames.practice.arena.impl.mlgrush.StandaloneMLGRushArena
import net.lyragames.practice.arena.menu.ArenaManageMenu
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.Cuboid
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
    @Command(name = "arena", aliases = ["arena help"])
    fun help(@Sender player: Player) {
        player.sendMessage("${CC.PRIMARY}Arena Commands:")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/arena create <name> <type>")
        player.sendMessage("${CC.SECONDARY}/arena type <name> <type> ${CC.GRAY}- Sumo, MLGRush, BedFight, Bridge, Build and Normal")
        player.sendMessage("${CC.SECONDARY}/arena delete <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos1 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena pos2 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena min <arena>")
        player.sendMessage("${CC.SECONDARY}/arena max <arena>")
        player.sendMessage("${CC.SECONDARY}/arena deadzone <arena> <deadzone> ${CC.GRAY}- set an arena's lowest Y location (Used for bridges, bedfight, etc)")
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
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.PRIMARY}Bridge Arena Command:")
        player.sendMessage(CC.translate("&7&m---------------------"))
        player.sendMessage("${CC.SECONDARY}/arena redspawn <arena>")
        player.sendMessage("${CC.SECONDARY}/arena bluespawn <arena>")
        player.sendMessage("${CC.SECONDARY}/arena redportal1 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena redportal2 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena blueportal1 <arena>")
        player.sendMessage("${CC.SECONDARY}/arena blueportal2 <arena>")
        player.sendMessage(CC.translate("&7&m---------------------"))
    }

    @Command(name = "arena create")
    @Permission("lpractice.command.arena.create")
    fun create(@Sender player: Player, name: String, type: ArenaType) {
        if (Arena.getByName(name) != null) {
            player.sendMessage(CC.RED + "That arena already exists!")
            return
        }

        val arena = when (type) {
            ArenaType.FIREBALL_FIGHT -> StandaloneFireBallFightArena(name)
            ArenaType.BRIDGE -> StandaloneBridgeArena(name)
            ArenaType.MLGRUSH -> StandaloneMLGRushArena(name)
            ArenaType.BEDFIGHT -> StandaloneBedWarsArena(name)
            else -> StandaloneArena(name)
        }

        arena.save()
        Arena.arenas.add(arena)

        player.sendMessage("${CC.PRIMARY}Successfully created ${CC.SECONDARY}$name${CC.PRIMARY} arena with ${CC.SECONDARY}${type.name}${CC.PRIMARY} type!")
    }

    @Command(name = "arena delete")
    @Permission("lpractice.command.arena.delete")
    fun delete(@Sender player: Player, arena: Arena) {
        arena.delete()
        Arena.arenas.remove(arena)

        player.sendMessage("${CC.PRIMARY}Successfully deleted ${CC.SECONDARY}${arena.name}${CC.PRIMARY}!")
    }

    @Command(name = "arena pos1", aliases = ["arena position1", "arena l1", "arena location1"])
    @Permission("lpractice.command.arena.setup")
    fun pos1(@Sender player: Player, arena: Arena) {
        arena.l1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 1!")
    }

    @Command(name = "arena pos2", description = "set an arena's second location")
    @Permission("lpractice.command.arena.setup")
    fun pos2(@Sender player: Player, arena: Arena) {
        arena.l2 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 2!")
    }

    @Command(name = "arena min", aliases = ["arena minimum"])
    @Permission("lpractice.command.arena.setup")
    fun min(@Sender player: Player, arena: Arena) {
        arena.min = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s min location!")

        if (arena.max != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }

    @Command(name = "arena max", aliases = ["arena maximum"])
    @Permission("lpractice.command.arena.setup")
    fun max(@Sender player: Player, arena: Arena) {
        arena.max = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s max location!")

        if (arena.min != null) {
            arena.bounds = Cuboid(arena.min, arena.max)
        }
    }


    @Command(name = "arena deadzone", aliases = ["arena yval"])
    @Permission("lpractice.command.arena.setup")
    fun deadzone(@Sender player: Player, arena: Arena, deadzone: Int) {
        arena.deadzone = deadzone
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s deadzone to ${CC.SECONDARY}$deadzone${CC.PRIMARY}!")
    }

    @Command(name = "arena type")
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
        }else if (type == ArenaType.BRIDGE) {
            val newArena = StandaloneBridgeArena(arena.name)
            newArena.l1 = arena.l1
            newArena.l2 = arena.l2
            newArena.deadzone = arena.deadzone
            newArena.min = arena.min
            newArena.max = arena.max
            newArena.arenaType = type

            if (arena is StandaloneArena) {
                for (duplicate in arena.duplicates) {
                    val newDuplicate = BridgeArena(duplicate.name)

                    newDuplicate.min = duplicate.min
                    newDuplicate.max = duplicate.max
                    newDuplicate.bluePortal1 = (duplicate as BridgeArena).bluePortal1
                    newDuplicate.bluePortal2 = duplicate.bluePortal2
                    newDuplicate.redPortal1 = duplicate.redPortal1
                    newDuplicate.redPortal2 = duplicate.redPortal2
                    newDuplicate.blueSpawn = duplicate.blueSpawn
                    newDuplicate.redSpawn = duplicate.redSpawn
                    newDuplicate.deadzone = duplicate.deadzone
                    newDuplicate.duplicate = true

                    newDuplicate.bounds = Cuboid(newDuplicate.min, newDuplicate.max)
                    newDuplicate.arenaType = ArenaType.BRIDGE

                    arena.duplicates.removeIf { it.name.equals(duplicate.name, false) }
                    arena.duplicates.add(newDuplicate)
                }
            }

            Arena.arenas.removeIf { it.name.equals(arena.name, false) }
            Arena.arenas.add(newArena)

            newArena.save()
        }else {
            arena.arenaType = type
            arena.save()
        }

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s type to ${CC.SECONDARY}${type.name}${CC.PRIMARY}!")
    }

    @Command(name = "arena bed1", aliases = ["arena b1"])
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

    @Command(name = "arena bed2", aliases = ["arena b2"])
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

    @Command(name = "arena redBed", aliases = ["arena rb"])
    @Permission("lpractice.command.arena.setup")
    fun redBed(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights & Fireball Fight arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).redBed = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red bed location!")
    }

    @Command(name = "arena blueBed", aliases = ["arena bb"])
    @Permission("lpractice.command.arena.setup")
    fun blueBed(@Sender player: Player, arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights & Fireball Fight arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).blueBed = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue bed location!")
    }

    @Command(name = "arena redspawn")
    @Permission("lpractice.command.arena.setup")
    fun redPos(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.BRIDGE && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights, Bridge and Fireball Fight arenas!")
            return
        }

        if (arena.arenaType == ArenaType.BEDFIGHT || arena.arenaType == ArenaType.FIREBALL_FIGHT) {
            (arena as StandaloneBedWarsArena).redSpawn = player.location
        }else {
            (arena as StandaloneBridgeArena).redSpawn = player.location
        }

        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red spawn location!")
    }

    @Command(name = "arena bluespawn")
    @Permission("lpractice.command.arena.setup")
    fun bluePos(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.BRIDGE && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights, Bridge and Fireball Fight arenas!")
            return
        }

        if (arena.arenaType == ArenaType.BEDFIGHT || arena.arenaType == ArenaType.FIREBALL_FIGHT) {
            (arena as StandaloneBedWarsArena).blueSpawn = player.location
        }else {
            (arena as StandaloneBridgeArena).blueSpawn = player.location
        }

        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue spawn location!")
    }

    @Command(name = "arena blueportal1")
    @Permission("lpractice.command.arena.setup")
    fun bluePortal1(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).bluePortal1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue portal position 1 location!")
    }

    @Command(name = "arena blueportal2")
    @Permission("lpractice.command.arena.setup")
    fun bluePortal2(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).bluePortal2 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue portal position 2 location!")
    }

    @Command(name = "arena redportal1")
    @Permission("lpractice.command.arena.setup")
    fun redPortal1(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).redPortal1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red portal position 1 location!")
    }

    @Command(name = "arena redportal2")
    @Permission("lpractice.command.arena.setup")
    fun redPortal2(@Sender player: Player, arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).redPortal2 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red portal position 2 location!")
    }

    @Command(name = "arena menu")
    @Permission("lpractice.command.arena.menu")
    fun manage(@Sender player: Player, arena: Arena) {
        ArenaManageMenu(arena).openMenu(player)
    }
}