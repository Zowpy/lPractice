package net.lyragames.practice.arena.impl.mlgrush

import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.WorldEditException
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard
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
import org.bukkit.Location
import java.util.concurrent.ThreadLocalRandom

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/28/2022
 * Project: lPractice
 */

class StandaloneMLGRushArena(name: String) : StandaloneArena(name) {

    var bed1: Location? = null
    var bed2: Location? = null

    override var arenaType = ArenaType.MLGRUSH
        get() = ArenaType.MLGRUSH

    override val isSetup: Boolean
        get() = l1 != null && l2 != null && min != null && max != null && bed1 != null && bed2 != null

    override fun save() {
        val configFile = PracticePlugin.instance.arenasFile
        val configSection = configFile.createSection("arenas.$name")

        configSection.set("l1", LocationUtil.serialize(l1))
        configSection.set("l2", LocationUtil.serialize(l2))
        configSection.set("min", LocationUtil.serialize(min))
        configSection.set("max", LocationUtil.serialize(max))
        configSection.set("bed1", LocationUtil.serialize(bed1))
        configSection.set("bed2", LocationUtil.serialize(bed2))
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
                configSection1.set("bed1", LocationUtil.serialize((arena as StandaloneMLGRushArena).bed1))
                configSection1.set("bed2", LocationUtil.serialize(arena.bed2))
                configSection1.set("deadzone", arena.deadzone)

                i++
            }
        }

        configFile.save()
    }

    override fun duplicate(world: org.bukkit.World, times: Int) {
        if (duplicate) return

        for (i in 0 until times) {
            val random = ThreadLocalRandom.current().nextDouble(10.0) + 1
            val offsetMultiplier = ThreadLocalRandom.current().nextDouble(10000.0) + 1

            val offsetX = random * offsetMultiplier / 10
            val offsetZ = random * offsetMultiplier / 10

            val minX = min!!.x + offsetX
            val minZ = min!!.z + offsetZ
            val maxX = max!!.x + offsetX
            val maxZ = max!!.z + offsetZ

            val aX = l1!!.x + offsetX
            val aZ = l1!!.z + offsetZ
            val bX = l2!!.x + offsetX
            val bZ = l2!!.z + offsetZ

            val b1X = bed1!!.x + offsetX
            val b1Z = bed1!!.z + offsetZ
            val b2X = bed2!!.x + offsetX
            val b2Z = bed2!!.z + offsetZ

            val min = Location(world, minX, bounds.lowerY.toDouble(), minZ)
            val max = Location(world, maxX, bounds.upperY.toDouble(), maxZ)

            val a = Location(world, aX, l1!!.y, aZ, l1!!.yaw, l1!!.pitch)
            val b = Location(world, bX, l2!!.y, bZ, l2!!.yaw, l2!!.pitch)

            val b1 = Location(world, b1X, bed1!!.y, b1Z)
            val b2 = Location(world, b2X, bed1!!.y, b2Z)

            val arena = StandaloneMLGRushArena(name + i)

            arena.bounds = Cuboid(min, max)
            arena.l1 = a
            arena.l2 = b
            arena.bed1 = b1
            arena.bed2 = b2
            arena.min = min
            arena.max = max
            arena.duplicate = true
            arena.deadzone = deadzone

            duplicates.add(arena)

            val weWorld = BukkitWorld(world)
            val worldData = weWorld.worldData

            val to = Vector(minX, bounds.lowerY.toDouble(), minZ)

            val clipboard = BlockArrayClipboard(
                CuboidRegion(
                    Vector(min.x, min.y, min.z),
                    Vector(max.x, max.y, max.z)
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