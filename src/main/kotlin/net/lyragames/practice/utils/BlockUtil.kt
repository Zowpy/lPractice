package net.lyragames.practice.utils

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import kotlin.math.abs

object BlockUtil {
    private var blockSolidPassSet: MutableSet<Byte> = mutableSetOf()
    private var blockStairsSet: MutableSet<Byte> = mutableSetOf()
    private var blockLiquidsSet: MutableSet<Byte> = mutableSetOf()
    private var blockWebsSet: MutableSet<Byte> = mutableSetOf()
    private var blockIceSet: MutableSet<Byte> = mutableSetOf()
    private var blockCarpetSet: MutableSet<Byte> = mutableSetOf()

    fun isOnStairs(location: Location, down: Int): Boolean {
        return isUnderBlock(location, blockStairsSet, down)
    }

    fun isOnLiquid(location: Location, down: Int): Boolean {
        return isUnderBlock(location, blockLiquidsSet, down)
    }

    fun isOnWeb(location: Location, down: Int): Boolean {
        return isUnderBlock(location, blockWebsSet, down)
    }

    fun isOnIce(location: Location, down: Int): Boolean {
        return isUnderBlock(location, blockIceSet, down)
    }

    fun isOnCarpet(location: Location, down: Int): Boolean {
        return isUnderBlock(location, blockCarpetSet, down)
    }

    private fun isUnderBlock(location: Location, itemIDs: Set<Byte>?, down: Int): Boolean {
        val posX = location.x
        val posZ = location.z
        val fracX = if (posX % 1.0 > 0.0) Math.abs(posX % 1.0) else 1.0 - Math.abs(posX % 1.0)
        val fracZ = if (posZ % 1.0 > 0.0) Math.abs(posZ % 1.0) else 1.0 - Math.abs(posZ % 1.0)
        val blockX = location.blockX
        val blockY = location.blockY - down
        val blockZ = location.blockZ
        val world = location.world
        if (itemIDs!!.contains(world.getBlockAt(blockX, blockY, blockZ).typeId.toByte())) {
            return true
        }
        if (fracX < 0.3) {
            if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ).typeId.toByte())) {
                return true
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
            } else if (fracZ > 0.7) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
            }
        } else if (fracX > 0.7) {
            if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ).typeId.toByte())) {
                return true
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
            } else if (fracZ > 0.7) {
                if (itemIDs.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (itemIDs.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
            }
        } else if (fracZ < 0.3) {
            if (itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                return true
            }
        } else if (fracZ > 0.7 && itemIDs.contains(world.getBlockAt(blockX, blockY, blockZ + 1).typeId.toByte())) {
            return true
        }
        return false
    }

    fun isOnGround(location: Location, down: Int): Boolean {
        val posX = location.x
        val posZ = location.z
        val fracX = if (posX % 1.0 > 0.0) abs(posX % 1.0) else 1.0 - abs(posX % 1.0)
        val fracZ = if (posZ % 1.0 > 0.0) abs(posZ % 1.0) else 1.0 - abs(posZ % 1.0)
        val blockX = location.blockX
        val blockY = location.blockY - down
        val blockZ = location.blockZ
        val world = location.world
        if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ).typeId.toByte())) {
            return true
        }
        if (fracX < 0.3) {
            if (!blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ).typeId.toByte())) {
                return true
            }
            if (fracZ < 0.3) {
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
            } else if (fracZ > 0.7) {
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
            }
        } else if (fracX > 0.7) {
            if (!blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ).typeId.toByte())) {
                return true
            }
            if (fracZ < 0.3) {
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX - 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet!!.contains(world.getBlockAt(blockX + 1, blockY, blockZ - 1).typeId.toByte())) {
                    return true
                }
            } else if (fracZ > 0.7) {
                if (!blockSolidPassSet!!.contains(world.getBlockAt(blockX - 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
                if (!blockSolidPassSet.contains(world.getBlockAt(blockX + 1, blockY, blockZ + 1).typeId.toByte())) {
                    return true
                }
            }
        } else if (fracZ < 0.3) {
            if (!blockSolidPassSet.contains(world.getBlockAt(blockX, blockY, blockZ - 1).typeId.toByte())) {
                return true
            }
        } else if (fracZ > 0.7 && !blockSolidPassSet!!.contains(
                world.getBlockAt(blockX, blockY, blockZ + 1).typeId
                    .toByte()
            )
        ) {
            return true
        }
        return false
    }

    val blockFaces: Array<BlockFace>
    fun getNearbyEntities(l: Location, radius: Int): Array<Entity> {
        val chunkRadius = if (radius < 16) 1 else (radius - radius % 16) / 16
        val radiusEntities = HashSet<Entity>()
        try {
            for (chX in -chunkRadius..chunkRadius) {
                for (chZ in -chunkRadius..chunkRadius) {
                    val x = l.x.toInt()
                    val y = l.y.toInt()
                    val z = l.z.toInt()
                    for (e in Location(
                        l.world,
                        (x + chX * 16).toDouble(), y.toDouble(), (z + chZ * 16).toDouble()
                    ).chunk.entities) {
                        if (e.location.distance(l) <= radius && e.location.block !== l.block) radiusEntities.add(e)
                    }
                }
            }
        } catch (e: Exception) {
            //
        }
        return radiusEntities.toTypedArray()
    }

    fun generatesCobble(id: Int, b: Block): Boolean {
        val mirrorID1 = if (id == 8 || id == 9) 10 else 8
        val mirrorID2 = if (id == 8 || id == 9) 11 else 9
        for (face in blockFaces) {
            val r = b.getRelative(face, 1)
            if (r.typeId == mirrorID1 || r.typeId == mirrorID2) {
                return true
            }
        }
        return false
    }

    init {
        blockSolidPassSet.add(0.toByte())
        blockSolidPassSet.add(6.toByte())
        blockSolidPassSet.add(8.toByte())
        blockSolidPassSet.add(9.toByte())
        blockSolidPassSet.add(10.toByte())
        blockSolidPassSet.add(11.toByte())
        blockSolidPassSet.add(27.toByte())
        blockSolidPassSet.add(28.toByte())
        blockSolidPassSet.add(30.toByte())
        blockSolidPassSet.add(31.toByte())
        blockSolidPassSet.add(32.toByte())
        blockSolidPassSet.add(37.toByte())
        blockSolidPassSet.add(38.toByte())
        blockSolidPassSet.add(39.toByte())
        blockSolidPassSet.add(40.toByte())
        blockSolidPassSet.add(50.toByte())
        blockSolidPassSet.add(51.toByte())
        blockSolidPassSet.add(55.toByte())
        blockSolidPassSet.add(59.toByte())
        blockSolidPassSet.add(63.toByte())
        blockSolidPassSet.add(66.toByte())
        blockSolidPassSet.add(68.toByte())
        blockSolidPassSet.add(69.toByte())
        blockSolidPassSet.add(70.toByte())
        blockSolidPassSet.add(72.toByte())
        blockSolidPassSet.add(75.toByte())
        blockSolidPassSet.add(76.toByte())
        blockSolidPassSet.add(77.toByte())
        blockSolidPassSet.add(78.toByte())
        blockSolidPassSet.add(83.toByte())
        blockSolidPassSet.add(90.toByte())
        blockSolidPassSet.add(104.toByte())
        blockSolidPassSet.add(105.toByte())
        blockSolidPassSet.add(115.toByte())
        blockSolidPassSet.add(119.toByte())
        blockSolidPassSet.add((-124).toByte())
        blockSolidPassSet.add((-113).toByte())
        blockSolidPassSet.add((-81).toByte())
        blockStairsSet.add(53.toByte())
        blockStairsSet.add(67.toByte())
        blockStairsSet.add(108.toByte())
        blockStairsSet.add(109.toByte())
        blockStairsSet.add(114.toByte())
        blockStairsSet.add((-128).toByte())
        blockStairsSet.add((-122).toByte())
        blockStairsSet.add((-121).toByte())
        blockStairsSet.add((-120).toByte())
        blockStairsSet.add((-100).toByte())
        blockStairsSet.add((-93).toByte())
        blockStairsSet.add((-92).toByte())
        blockStairsSet.add((-76).toByte())
        blockStairsSet.add(126.toByte())
        blockStairsSet.add((-74).toByte())
        blockStairsSet.add(44.toByte())
        blockStairsSet.add(78.toByte())
        blockStairsSet.add(99.toByte())
        blockStairsSet.add((-112).toByte())
        blockStairsSet.add((-115).toByte())
        blockStairsSet.add((-116).toByte())
        blockStairsSet.add((-105).toByte())
        blockStairsSet.add((-108).toByte())
        blockStairsSet.add(100.toByte())
        blockLiquidsSet.add(8.toByte())
        blockLiquidsSet.add(9.toByte())
        blockLiquidsSet.add(10.toByte())
        blockLiquidsSet.add(11.toByte())
        blockWebsSet.add(30.toByte())
        blockIceSet.add(79.toByte())
        blockIceSet.add((-82).toByte())
        blockCarpetSet.add((-85).toByte())
    }

    init {
        blockFaces = arrayOf(
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST
        )
    }
}
