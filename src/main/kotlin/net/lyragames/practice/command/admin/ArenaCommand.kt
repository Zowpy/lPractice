package net.lyragames.practice.command.admin

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */
@CommandAlias("arena")
object ArenaCommand: BaseCommand() {

    @CommandPermission("lpractice.command.arena.help")
    @HelpCommand
    @Syntax("[page]")
    fun help(help: CommandHelp) {
        help.showHelp()
        /*
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

         */
    }

    @Subcommand("create")
    @CommandPermission("lpractice.command.arena.create")
    @Description("Creates an arena")
    fun create(player: CommandSender, @Single @Name("arena") name: String, @Single @Name("type") type: ArenaType) {
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

    @Subcommand("delete")
    @CommandPermission("lpractice.command.arena.delete")
    @Description("Deletes an arena")

    fun delete(player: CommandSender, @Name("arena") arena: Arena) {
        arena.delete()
        Arena.arenas.remove(arena)

        player.sendMessage("${CC.PRIMARY}Successfully deleted ${CC.SECONDARY}${arena.name}${CC.PRIMARY}!")
    }

    @Subcommand("pos1|position1|l1|location1")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Position of player one in the arena")

    fun pos1(player: CommandSender, @Single @Name("arena") arena: Arena) {
        arena.l1 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 1!")
    }

    @Subcommand("pos2|position2|l2|location2")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Position of player two in the arena")
    fun pos2(player: CommandSender, @Single @Name("arena") arena: Arena) {
        arena.l2 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s location 2!")
    }

    @Subcommand("min|minimum")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Minimum point of the arena")

    fun min(player: CommandSender, @Single @Name("arena") arena: Arena) {
        arena.min = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s min location!")

        if (arena.max != null) {
            arena.bounds = Cuboid(arena.min!!, arena.max!!)
        }
    }

    @Subcommand("max|maximum")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Maximum point in the arena")
    fun max(player: CommandSender, @Single @Name("arena") arena: Arena) {
        arena.max = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s max location!")

        if (arena.min != null) {
            arena.bounds = Cuboid(arena.min!!, arena.max!!)
        }
    }


    @Subcommand("deadzone|yval")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Deadzone of an arena")

    fun deadzone(player: CommandSender,  @Single @Name("arena") arena: Arena, @Single @Name("deadzone") deadzone: Int) {
        arena.deadzone = deadzone
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s deadzone to ${CC.SECONDARY}$deadzone${CC.PRIMARY}!")
    }

    @Subcommand("arena type")
    @Description("Set the type of the arena")

    fun type(player: CommandSender,@Single @Name("arena") arena: Arena, type: ArenaType) {
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

                    newDuplicate.bounds = Cuboid(newDuplicate.min!!, newDuplicate.max!!)
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

                    newDuplicate.bounds = Cuboid(newDuplicate.min!!, newDuplicate.max!!)
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

                    newDuplicate.bounds = Cuboid(newDuplicate.min!!, newDuplicate.max!!)
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

    @Subcommand("bed1|b1")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Bed one for the MGLRush game")
    fun bed1(player: CommandSender, @Single @Name("arena") arena: Arena) {
        if (arena.arenaType != ArenaType.MLGRUSH) {
            player.sendMessage("${CC.RED}This command is only supported for MLGRush arenas!")
            return
        }
        (arena as StandaloneMLGRushArena).bed1 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s bed location 1!")
    }

    @Subcommand("bed2|b2")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Bed two of the MGLRush arena")

    fun bed2(player: CommandSender, @Single @Name("arena") arena: Arena) {
        if (arena.arenaType != ArenaType.MLGRUSH) {
            player.sendMessage("${CC.RED}This command is only supported for MLGRush arenas!")
            return
        }
        (arena as StandaloneMLGRushArena).bed2 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s bed location 2!")
    }

    @Subcommand("redBed|rb")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the red bed for Fireball Fights or Bed Fights")
    fun redBed( player: CommandSender, @Single @Name("arena") arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights & Fireball Fight arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).redBed = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red bed location!")
    }

    @Subcommand("blueBed|bb")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the blue bed for Fireball Fights or Bed Fights")
    fun blueBed(player: CommandSender, @Single @Name("arena") arena: Arena) {
        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights & Fireball Fight arenas!")
            return
        }

        (arena as StandaloneBedWarsArena).blueBed = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue bed location!")
    }

    @Subcommand("redspawn")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the red spawn for Fireball Fights or Bed Fights")

    fun redPos(player: CommandSender,  @Single @Name("arena") arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.BRIDGE && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights, Bridge and Fireball Fight arenas!")
            return
        }

        if (arena.arenaType == ArenaType.BEDFIGHT || arena.arenaType == ArenaType.FIREBALL_FIGHT) {
            (arena as StandaloneBedWarsArena).redSpawn = (player as Player).location
        }else {
            (arena as StandaloneBridgeArena).redSpawn = (player as Player).location
        }

        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red spawn location!")
    }

    @Subcommand("bluespawn")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the blue spawn for Fireball Fights or Bed Fights")

    fun bluePos( player: CommandSender,  @Single @Name("arena") arena: Arena) {

        if (arena.arenaType != ArenaType.BEDFIGHT && arena.arenaType != ArenaType.BRIDGE && arena.arenaType != ArenaType.FIREBALL_FIGHT) {
            player.sendMessage("${CC.RED}This command is only supported for Bed Fights, Bridge and Fireball Fight arenas!")
            return
        }

        if (arena.arenaType == ArenaType.BEDFIGHT || arena.arenaType == ArenaType.FIREBALL_FIGHT) {
            (arena as StandaloneBedWarsArena).blueSpawn = (player as Player).location
        }else {
            (arena as StandaloneBridgeArena).blueSpawn = (player as Player).location
        }

        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue spawn location!")
    }

    @Subcommand("blueportal1")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Sets the blue portal for Bridges")

    fun bluePortal1(player: CommandSender,  @Single @Name("arena") arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).bluePortal1 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue portal position 1 location!")
    }

    @Subcommand("blueportal2")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the blue portal for Bridges")

    fun bluePortal2(player: CommandSender, arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).bluePortal2 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s blue portal position 2 location!")
    }

    @Subcommand("redportal1")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the red portal for Bridges")

    fun redPortal1(player: Player,  @Single @Name("arena") arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).redPortal1 = player.location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red portal position 1 location!")
    }

    @Subcommand("redportal2")
    @CommandPermission("lpractice.command.arena.setup")
    @Description("Set the red portal for Bridges")

    fun redPortal2(player: CommandSender,  @Single @Name("arena") arena: Arena) {

        if (arena.arenaType != ArenaType.BRIDGE) {
            player.sendMessage("${CC.RED}This command is only supported for Bridges arenas!")
            return
        }

        (arena as StandaloneBridgeArena).redPortal2 = (player as Player).location
        arena.save()

        player.sendMessage("${CC.PRIMARY}Successfully set ${CC.SECONDARY}${arena.name}${CC.PRIMARY}'s red portal position 2 location!")
    }

    @Subcommand("menu")
    @CommandPermission("lpractice.command.arena.menu")
    @Description("Opens the arena menu")

    fun manage(player: CommandSender, @Single @Name("arena") arena: Arena) {
        ArenaManageMenu(arena).openMenu(player as Player)
    }
}