package net.lyragames.practice.arena.impl.bridge

import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.mask.ExistingBlockMask
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.impl.StandaloneArena
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.utils.LocationHelper
import org.bukkit.Location
import org.bukkit.World
import java.util.concurrent.ThreadLocalRandom

class StandaloneBridgeArena(name: String) : StandaloneArena(name) {

    var redSpawn: Location? = null
    var redPortal1: Location? = null
    var redPortal2: Location? = null

    var blueSpawn: Location? = null
    var bluePortal1: Location? = null
    var bluePortal2: Location? = null

    var bluePortal: Cuboid? = null
    var redPortal: Cuboid? = null

    override var arenaType = ArenaType.BRIDGE
        get() = ArenaType.BRIDGE

    override val isSetup: Boolean
        get() = min != null && max != null && redSpawn != null && redPortal != null
                && blueSpawn != null && bluePortal != null

    override fun save() {
        val configFile = PracticePlugin.instance.arenasFile
        val configSection = configFile.createSection("arenas.$name")

        configSection.set("redSpawn", LocationUtil.serialize(redSpawn))
        configSection.set("redPortal1", LocationUtil.serialize(redPortal1))
        configSection.set("redPortal2", LocationUtil.serialize(redPortal2))
        configSection.set("blueSpawn", LocationUtil.serialize(blueSpawn))
        configSection.set("bluePortal1", LocationUtil.serialize(bluePortal1))
        configSection.set("bluePortal2", LocationUtil.serialize(bluePortal2))

        configSection.set("min", LocationUtil.serialize(min))
        configSection.set("max", LocationUtil.serialize(max))
        configSection.set("deadzone", deadzone)
        configSection.set("type", arenaType.name)

        if (redPortal1 != null && redPortal2 != null) {
            redPortal = Cuboid(redPortal1, redPortal2)
        }

        if (bluePortal1 != null && bluePortal2 != null) {
            bluePortal = Cuboid(bluePortal1, bluePortal2)
        }

        if (duplicates.isNotEmpty()) {
            var i = 1

            for (arena in duplicates) {
                val configSection1 = configSection.createSection("duplicates.$i")

                configSection1.set("redSpawn", LocationUtil.serialize((arena as BridgeArena).redSpawn))
                configSection1.set("redPortal1", LocationUtil.serialize(arena.redPortal1))
                configSection1.set("redPortal2", LocationUtil.serialize(arena.redPortal2))
                configSection1.set("blueSpawn", LocationUtil.serialize(arena.blueSpawn))
                configSection1.set("bluePortal1", LocationUtil.serialize(arena.bluePortal1))
                configSection1.set("bluePortal2", LocationUtil.serialize(arena.bluePortal2))

                configSection1.set("min", LocationUtil.serialize(arena.min))
                configSection1.set("max", LocationUtil.serialize(arena.max))
                configSection1.set("deadzone", arena.deadzone)

                i++
            }
        }

        configFile.save()
    }

    override fun duplicate(world: World, times: Int) {
        for (i in 0 until times) {
            val random: Double = ThreadLocalRandom.current().nextDouble(10.0) + 1
            val offsetMultiplier: Double = ThreadLocalRandom.current().nextDouble(10000.0) + 1

            val offsetX: Double = random * offsetMultiplier / 10
            val offsetZ: Double = random * offsetMultiplier / 10

            val arena = BridgeArena(name + i)

            arena.l1 = LocationHelper.getLocation(world, l1!!).add(offsetX, 0.0, offsetZ)
            arena.l2 = LocationHelper.getLocation(world, l2!!).add(offsetX, 0.0, offsetZ)
            arena.min = LocationHelper.getLocation(world, min!!).add(offsetX, 0.0, offsetZ)
            arena.max = LocationHelper.getLocation(world, max!!).add(offsetX, 0.0, offsetZ)

            arena.bounds = Cuboid(arena.min, arena.max)

            arena.duplicate = true
            arena.deadzone = deadzone

            arena.redSpawn = LocationHelper.getLocation(world, this.redSpawn!!).add(offsetX, 0.0, offsetZ)
            arena.redPortal1 = LocationHelper.getLocation(world, this.redPortal1!!).add(offsetX, 0.0, offsetZ)
            arena.redPortal2 = LocationHelper.getLocation(world, this.redPortal2!!).add(offsetX, 0.0, offsetZ)

            arena.blueSpawn = LocationHelper.getLocation(world, this.blueSpawn!!).add(offsetX, 0.0, offsetZ)
            arena.bluePortal1 = LocationHelper.getLocation(world, this.bluePortal1!!).add(offsetX, 0.0, offsetZ)
            arena.bluePortal2 = LocationHelper.getLocation(world, this.bluePortal2!!).add(offsetX, 0.0, offsetZ)

            duplicates.add(arena)

            val weWorld = BukkitWorld(world)
            val worldData = weWorld.worldData

            val to = Vector(arena.min!!.x, bounds.lowerY.toDouble(), arena.min!!.z)

            val clipboard: Clipboard = BlockArrayClipboard(
                CuboidRegion(
                    Vector(arena.min!!.x, arena.min!!.y, arena.min!!.z),
                    Vector(arena.max!!.x, arena.max!!.y, arena.max!!.z)
                )
            )

            val destination = WorldEdit.getInstance().editSessionFactory.getEditSession(weWorld, -1)
            val copy = ForwardExtentCopy(clipboard, clipboard.region, clipboard.origin, destination, to)

            copy.sourceMask = ExistingBlockMask(clipboard)

            val operation = ClipboardHolder(clipboard, worldData)
                .createPaste(destination, worldData)
                .ignoreAirBlocks(false)
                .to(to)
                .build()

            try {
                Operations.completeLegacy(operation)
            } catch (e: WorldEditException) {
                e.printStackTrace()
            }
        }

        save()
    }
}