package net.lyragames.practice.arena.impl.bedwars

import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.Extent
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.function.mask.ExistingBlockMask
import com.sk89q.worldedit.function.operation.ForwardExtentCopy
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.World
import com.sk89q.worldedit.world.registry.WorldData
import net.lyragames.llib.utils.Cuboid
import net.lyragames.llib.utils.LocationUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.impl.StandaloneArena
import org.bukkit.Location
import java.util.concurrent.ThreadLocalRandom


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/31/2022
 * Project: lPractice
 */

class StandaloneBedWarsArena(name: String) : StandaloneArena(name) {

    var redBed: Location? = null
    var blueBed: Location? = null

    var blueSpawn: Location? = null
    var redSpawn: Location? = null

    override val isSetup: Boolean
        get() = blueSpawn != null && redSpawn != null && min != null && max != null && redBed != null && blueBed != null

    override fun save() {
        val configFile = PracticePlugin.instance.arenasFile
        val configSection = configFile.createSection("arenas.$name")

        configSection.set("blueSpawn", LocationUtil.serialize(blueSpawn))
        configSection.set("redSpawn", LocationUtil.serialize(redSpawn))
        configSection.set("min", LocationUtil.serialize(min))
        configSection.set("max", LocationUtil.serialize(max))
        configSection.set("redBed", LocationUtil.serialize(redBed))
        configSection.set("blueBed", LocationUtil.serialize(blueBed))
        configSection.set("deadzone", deadzone)
        configSection.set("type", arenaType.name)

        if (duplicates.isNotEmpty()) {
            var i = 1

            for (arena in duplicates) {
                val configSection1 = configSection.createSection("duplicates.$i")

                configSection1.set("l1", LocationUtil.serialize(arena.l1))
                configSection1.set("l2", LocationUtil.serialize(arena.l2))
                configSection1.set("min", LocationUtil.serialize(arena.min))
                configSection1.set("max", LocationUtil.serialize(arena.max))
                configSection.set("redBed", LocationUtil.serialize((arena as BedWarsArena).redBed))
                configSection.set("blueBed", LocationUtil.serialize(arena.blueBed))
                configSection1.set("deadzone", arena.deadzone)

                i++
            }
        }

        configFile.save()
    }

    override fun duplicate(world: org.bukkit.World, times: Int) {
        for (i in 0 until times) {
            val random: Double = ThreadLocalRandom.current().nextDouble(10.0) + 1
            val offsetMultiplier: Double = ThreadLocalRandom.current().nextDouble(10000.0) + 1

            val offsetX: Double = random * offsetMultiplier / 10
            val offsetZ: Double = random * offsetMultiplier / 10

            val minX: Double = bounds.lowerX + offsetX
            val minZ: Double = bounds.lowerZ + offsetZ
            val maxX: Double = bounds.upperX + offsetX
            val maxZ: Double = bounds.upperZ + offsetZ

            val aX: Double = redSpawn!!.x + offsetX
            val aZ: Double = redSpawn!!.z + offsetZ
            val bX: Double = blueSpawn!!.x + offsetX
            val bZ: Double = blueSpawn!!.z + offsetZ

            val b1X = redBed!!.x + offsetX
            val b1Z = redBed!!.z + offsetZ
            val b2X = blueBed!!.x + offsetX
            val b2Z = blueBed!!.z + offsetZ

            val min = Location(world, minX, bounds.lowerY.toDouble(), minZ)
            val max = Location(world, maxX, bounds.upperY.toDouble(), maxZ)

            val a = Location(world, aX, redSpawn!!.y, aZ, redSpawn!!.yaw, redSpawn!!.pitch)
            val b = Location(world, bX, blueSpawn!!.y, bZ, blueSpawn!!.yaw, blueSpawn!!.pitch)

            val b1 = Location(world, b1X, redBed!!.y, b1Z)
            val b2 = Location(world, b2X, redBed!!.y, b2Z)

            val arena = BedWarsArena(name + i)
            arena.bounds = Cuboid(min, max)
            arena.l1 = a
            arena.l2 = b
            arena.redBed = b1
            arena.blueBed = b2
            arena.duplicate = true
            arena.deadzone = deadzone
            duplicates.add(arena)

            val weWorld: World = BukkitWorld(world)
            val worldData: WorldData = weWorld.worldData
            val to = Vector(minX, bounds.lowerY.toDouble(), minZ)
            val clipboard: Clipboard = BlockArrayClipboard(
                CuboidRegion(
                    Vector(min.x, min.y, min.z),
                    Vector(max.x, max.y, max.z)
                )
            )
            val destination: Extent = WorldEdit.getInstance().editSessionFactory.getEditSession(weWorld, -1)
            val copy = ForwardExtentCopy(clipboard, clipboard.region, clipboard.origin, destination, to)
            copy.sourceMask = ExistingBlockMask(clipboard)
            val operation: Operation = ClipboardHolder(clipboard, worldData)
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