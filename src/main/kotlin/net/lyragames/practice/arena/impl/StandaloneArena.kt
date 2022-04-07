package net.lyragames.practice.arena.impl

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
import net.lyragames.practice.arena.Arena
import org.bukkit.Location
import java.util.concurrent.ThreadLocalRandom


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

open class StandaloneArena(name: String) : Arena(name) {

    open val duplicates: MutableList<Arena> = mutableListOf()

    override fun save() {
        val configFile = PracticePlugin.instance.arenasFile
        val configSection = configFile.createSection("arenas.$name")

        configSection.set("l1", LocationUtil.serialize(l1))
        configSection.set("l2", LocationUtil.serialize(l2))
        configSection.set("min", LocationUtil.serialize(min))
        configSection.set("max", LocationUtil.serialize(max))
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
            
            val aX: Double = l1!!.x + offsetX
            val aZ: Double = l1!!.z + offsetZ
            val bX: Double = l2!!.x + offsetX
            val bZ: Double = l2!!.z + offsetZ
            
            val min = Location(world, minX, bounds.lowerY.toDouble(), minZ)
            val max = Location(world, maxX, bounds.upperY.toDouble(), maxZ)
            
            val a = Location(world, aX, l1!!.y, aZ, l1!!.yaw, l1!!.pitch)
            val b = Location(world, bX, l2!!.y, bZ, l2!!.yaw, l2!!.pitch)
            
            val arena = Arena(name + i)
            arena.bounds = Cuboid(min, max)
            arena.l1 = a
            arena.l2 = b
            arena.min = min
            arena.max = max
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

    override fun isFree(): Boolean {
        return free || duplicates.stream().anyMatch { it.free && it.isSetup }
    }

    fun getFreeDuplicate(): Arena? {
        return duplicates.stream().filter { it.free && it.isSetup }.findAny().orElse(null)
    }
}