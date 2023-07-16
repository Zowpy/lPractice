package net.lyragames.practice.utils

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player
import kotlin.math.abs

class Cuboid : Iterable<Block?>, Cloneable, ConfigurationSerializable {
    private val worldName: String?

    /**
     * Get the minimum X co-ordinate of this Cuboid
     *
     * @return the minimum X co-ordinate
     */
    val lowerX: Int

    /**
     * Get the minimum Y co-ordinate of this Cuboid
     *
     * @return the minimum Y co-ordinate
     */
    val lowerY: Int

    /**
     * Get the minimum Z co-ordinate of this Cuboid
     *
     * @return the minimum Z co-ordinate
     */
    val lowerZ: Int

    /**
     * Get the maximum X co-ordinate of this Cuboid
     *
     * @return the maximum X co-ordinate
     */
    val upperX: Int

    /**
     * Get the maximum Y co-ordinate of this Cuboid
     *
     * @return the maximum Y co-ordinate
     */
    val upperY: Int

    /**
     * Get the maximum Z co-ordinate of this Cuboid
     *
     * @return the maximum Z co-ordinate
     */
    val upperZ: Int

    var l1: Location? = null

    var l2: Location? = null

    /**
     * Construct a Cuboid given two Location objects which represent any two corners of the Cuboid.
     * Note: The 2 locations must be on the same world.
     *
     * @param l1 - One of the corners
     * @param l2 - The other corner
     */
    constructor(l1: Location, l2: Location) {
        require(l1.world == l2.world) { "Locations must be on the same world" }
        worldName = l1.world.name
        lowerX = l1.blockX.coerceAtMost(l2.blockX)
        lowerY = l1.blockY.coerceAtMost(l2.blockY)
        lowerZ = l1.blockZ.coerceAtMost(l2.blockZ)
        upperX = l1.blockX.coerceAtLeast(l2.blockX)
        upperY = l1.blockY.coerceAtLeast(l2.blockY)
        upperZ = l1.blockZ.coerceAtLeast(l2.blockZ)
        this.l1 = l1
        this.l2 = l2
    }

    /**
     * Copy constructor.
     *
     * @param other - The Cuboid to copy
     */
    constructor(other: Cuboid) : this(
        other.world.name,
        other.lowerX,
        other.lowerY,
        other.lowerZ,
        other.upperX,
        other.upperY,
        other.upperZ
    ) {
    }

    /**
     * Construct a Cuboid in the given World and xyz co-ordinates
     *
     * @param world - The Cuboid's world
     * @param x1    - X co-ordinate of corner 1
     * @param y1    - Y co-ordinate of corner 1
     * @param z1    - Z co-ordinate of corner 1
     * @param x2    - X co-ordinate of corner 2
     * @param y2    - Y co-ordinate of corner 2
     * @param z2    - Z co-ordinate of corner 2
     */
    constructor(
        world: World,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        worldName = world.name
        lowerX = x1.coerceAtMost(x2)
        upperX = x1.coerceAtLeast(x2)
        lowerY = y1.coerceAtMost(y2)
        upperY = y1.coerceAtLeast(y2)
        lowerZ = z1.coerceAtMost(z2)
        upperZ = z1.coerceAtLeast(z2)
    }

    /**
     * Construct a Cuboid in the given world name and xyz co-ordinates.
     *
     * @param worldName - The Cuboid's world name
     * @param x1        - X co-ordinate of corner 1
     * @param y1        - Y co-ordinate of corner 1
     * @param z1        - Z co-ordinate of corner 1
     * @param x2        - X co-ordinate of corner 2
     * @param y2        - Y co-ordinate of corner 2
     * @param z2        - Z co-ordinate of corner 2
     */
    private constructor(
        worldName: String?,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) {
        this.worldName = worldName
        lowerX = x1.coerceAtMost(x2)
        upperX = x1.coerceAtLeast(x2)
        lowerY = y1.coerceAtMost(y2)
        upperY = y1.coerceAtLeast(y2)
        lowerZ = z1.coerceAtMost(z2)
        upperZ = z1.coerceAtLeast(z2)
    }

    /**
     * Construct a Cuboid using a map with the following keys: worldName, x1, x2, y1, y2, z1, z2
     *
     * @param map - The map of keys.
     */
    constructor(map: Map<String?, Any?>) {
        worldName = map["worldName"] as String?
        lowerX = (map["x1"] as Int?)!!
        upperX = (map["x2"] as Int?)!!
        lowerY = (map["y1"] as Int?)!!
        upperY = (map["y2"] as Int?)!!
        lowerZ = (map["z1"] as Int?)!!
        upperZ = (map["z2"] as Int?)!!
    }

    fun isInCuboid(location: Location): Boolean {
        return this.contains(location)
    }

    fun isInCuboid(p: Player): Boolean {
        return this.isInCuboid(p.location)
    }

    override fun serialize(): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["worldName"] = worldName!!
        map["x1"] = lowerX
        map["y1"] = lowerY
        map["z1"] = lowerZ
        map["x2"] = upperX
        map["y2"] = upperY
        map["z2"] = upperZ
        return map
    }

    /**
     * Get the Location of the lower northeast corner of the Cuboid (minimum XYZ co-ordinates).
     *
     * @return Location of the lower northeast corner
     */
    val lowerNE: Location
        get() = Location(world, lowerX.toDouble(), lowerY.toDouble(), lowerZ.toDouble())

    /**
     * Get the Location of the upper southwest corner of the Cuboid (maximum XYZ co-ordinates).
     *
     * @return Location of the upper southwest corner
     */
    val upperSW: Location
        get() = Location(world, upperX.toDouble(), upperY.toDouble(), upperZ.toDouble())

    /**
     * Get the blocks in the Cuboid.
     *
     * @return The blocks in the Cuboid
     */
    val blocks: List<Block>
        get() {
            val blockI: Iterator<Block> = this.iterator()
            val copy: MutableList<Block> = ArrayList()
            while (blockI.hasNext()) copy.add(blockI.next())
            return copy
        }

    /**
     * Get the the centre of the Cuboid.
     *
     * @return Location at the centre of the Cuboid
     */
    val center: Location
        get() {
            val x1 = upperX + 1
            val y1 = upperY + 1
            val z1 = upperZ + 1
            return Location(
                world,
                lowerX + (x1 - lowerX) / 2.0,
                lowerY + (y1 - lowerY) / 2.0,
                lowerZ + (z1 - lowerZ) / 2.0
            )
        }

    /**
     * Get the Cuboid's world.
     *
     * @return The World object representing this Cuboid's world
     * @throws IllegalStateException if the world is not loaded
     */
    val world: World
        get() = Bukkit.getWorld(worldName)
            ?: throw IllegalStateException("World '$worldName' is not loaded")

    /**
     * Get the size of this Cuboid along the X axis
     *
     * @return Size of Cuboid along the X axis
     */
    val sizeX: Int
        get() = upperX - lowerX + 1

    /**
     * Get the size of this Cuboid along the Y axis
     *
     * @return Size of Cuboid along the Y axis
     */
    val sizeY: Int
        get() = upperY - lowerY + 1

    /**
     * Get the size of this Cuboid along the Z axis
     *
     * @return Size of Cuboid along the Z axis
     */
    val sizeZ: Int
        get() = upperZ - lowerZ + 1

    /**
     * Get the Blocks at the eight corners of the Cuboid.
     *
     * @return essentials of Block objects representing the Cuboid corners
     */
    fun corners(): Array<Block?> {
        val res = arrayOfNulls<Block>(8)
        val w = world
        res[0] = w.getBlockAt(lowerX, lowerY, lowerZ)
        res[1] = w.getBlockAt(lowerX, lowerY, upperZ)
        res[2] = w.getBlockAt(lowerX, upperY, lowerZ)
        res[3] = w.getBlockAt(lowerX, upperY, upperZ)
        res[4] = w.getBlockAt(upperX, lowerY, lowerZ)
        res[5] = w.getBlockAt(upperX, lowerY, upperZ)
        res[6] = w.getBlockAt(upperX, upperY, lowerZ)
        res[7] = w.getBlockAt(upperX, upperY, upperZ)
        return res
    }

    /**
     * Expand the Cuboid in the given direction by the given amount.  Negative amounts will shrink the Cuboid in the given direction.  Shrinking a cuboid's face past the opposite face is not an error and will return a valid Cuboid.
     *
     * @param dir    - The direction in which to expand
     * @param amount - The number of blocks by which to expand
     * @return A new Cuboid expanded by the given direction and amount
     */
    fun expand(dir: CuboidDirection, amount: Int): Cuboid {
        return when (dir) {
            CuboidDirection.North -> Cuboid(
                worldName,
                lowerX - amount,
                lowerY,
                lowerZ,
                upperX,
                upperY,
                upperZ
            )
            CuboidDirection.South -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                upperX + amount,
                upperY,
                upperZ
            )
            CuboidDirection.East -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ - amount,
                upperX,
                upperY,
                upperZ
            )
            CuboidDirection.West -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                upperX,
                upperY,
                upperZ + amount
            )
            CuboidDirection.Down -> Cuboid(
                worldName,
                lowerX,
                lowerY - amount,
                lowerZ,
                upperX,
                upperY,
                upperZ
            )
            CuboidDirection.Up -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                upperX,
                upperY + amount,
                upperZ
            )
            else -> throw IllegalArgumentException("Invalid direction $dir")
        }
    }

    /**
     * Shift the Cuboid in the given direction by the given amount.
     *
     * @param dir    - The direction in which to shift
     * @param amount - The number of blocks by which to shift
     * @return A new Cuboid shifted by the given direction and amount
     */
    fun shift(dir: CuboidDirection, amount: Int): Cuboid {
        return expand(dir, amount).expand(dir.opposite(), -amount)
    }

    /**
     * Outset (grow) the Cuboid in the given direction by the given amount.
     *
     * @param dir    - The direction in which to outset (must be Horizontal, Vertical, or Both)
     * @param amount - The number of blocks by which to outset
     * @return A new Cuboid outset by the given direction and amount
     */
    fun outset(dir: CuboidDirection, amount: Int): Cuboid {
        val c = when (dir) {
            CuboidDirection.Horizontal -> expand(CuboidDirection.North, amount).expand(CuboidDirection.South, amount)
                .expand(CuboidDirection.East, amount).expand(CuboidDirection.West, amount)
            CuboidDirection.Vertical -> expand(CuboidDirection.Down, amount).expand(CuboidDirection.Up, amount)
            CuboidDirection.Both -> outset(CuboidDirection.Horizontal, amount).outset(CuboidDirection.Vertical, amount)
            else -> throw IllegalArgumentException("Invalid direction $dir")
        }
        return c
    }

    /**
     * Inset (shrink) the Cuboid in the given direction by the given amount.  Equivalent
     * to calling outset() with a negative amount.
     *
     * @param dir    - The direction in which to inset (must be Horizontal, Vertical, or Both)
     * @param amount - The number of blocks by which to inset
     * @return A new Cuboid inset by the given direction and amount
     */
    fun inset(dir: CuboidDirection, amount: Int): Cuboid {
        return outset(dir, -amount)
    }

    /**
     * Return true if the point at (x,y,z) is contained within this Cuboid.
     *
     * @param x - The X co-ordinate
     * @param y - The Y co-ordinate
     * @param z - The Z co-ordinate
     * @return true if the given point is within this Cuboid, false otherwise
     */
    fun contains(x: Int, y: Int, z: Int): Boolean {
        return x in lowerX..upperX && y >= lowerY && y <= upperY && z >= lowerZ && z <= upperZ
    }

    /**
     * Check if the given Block is contained within this Cuboid.
     *
     * @param b - The Block to check for
     * @return true if the Block is within this Cuboid, false otherwise
     */
    operator fun contains(b: Block): Boolean {
        return this.contains(b.location)
    }

    /**
     * Check if the given Location is contained within this Cuboid.
     *
     * @param l - The Location to check for
     * @return true if the Location is within this Cuboid, false otherwise
     */
    operator fun contains(l: Location): Boolean {
        return if (worldName != l.world.name) false else this.contains(l.blockX, l.blockY, l.blockZ)
    }

    /**
     * Get the volume of this Cuboid.
     *
     * @return The Cuboid volume, in blocks
     */
    val volume: Int
        get() = sizeX * sizeY * sizeZ

    /**
     * Get the average light level of all empty (air) blocks in the Cuboid.  Returns 0 if there are no empty blocks.
     *
     * @return The average light level of this Cuboid
     */
    val averageLightLevel: Byte
        get() {
            var total: Long = 0
            var n = 0
            for (b in this) {
                if (b.isEmpty) {
                    total += b.lightLevel.toLong()
                    ++n
                }
            }
            return if (n > 0) (total / n).toByte() else 0
        }

    /**
     * Contract the Cuboid, returning a Cuboid with any air around the edges removed, just large enough to include all non-air blocks.
     *
     * @return A new Cuboid with no external air blocks
     */
    fun contract(): Cuboid {
        return this.contract(CuboidDirection.Down).contract(CuboidDirection.South).contract(CuboidDirection.East)
            .contract(CuboidDirection.Up).contract(CuboidDirection.North).contract(CuboidDirection.West)
    }

    /**
     * Contract the Cuboid in the given direction, returning a new Cuboid which has no exterior empty space.
     * E.g. A direction of Down will push the top face downwards as much as possible.
     *
     * @param dir - The direction in which to contract
     * @return A new Cuboid contracted in the given direction
     */
    fun contract(dir: CuboidDirection): Cuboid {
        var face = getFace(dir.opposite())
        return when (dir) {
            CuboidDirection.Down -> {
                while (face.containsOnly(0) && face.lowerY > lowerY) {
                    face = face.shift(CuboidDirection.Down, 1)
                }
                Cuboid(worldName, lowerX, lowerY, lowerZ, upperX, face.upperY, upperZ)
            }
            CuboidDirection.Up -> {
                while (face.containsOnly(0) && face.upperY < upperY) {
                    face = face.shift(CuboidDirection.Up, 1)
                }
                Cuboid(worldName, lowerX, face.lowerY, lowerZ, upperX, upperY, upperZ)
            }
            CuboidDirection.North -> {
                while (face.containsOnly(0) && face.lowerX > lowerX) {
                    face = face.shift(CuboidDirection.North, 1)
                }
                Cuboid(worldName, lowerX, lowerY, lowerZ, face.upperX, upperY, upperZ)
            }
            CuboidDirection.South -> {
                while (face.containsOnly(0) && face.upperX < upperX) {
                    face = face.shift(CuboidDirection.South, 1)
                }
                Cuboid(worldName, face.lowerX, lowerY, lowerZ, upperX, upperY, upperZ)
            }
            CuboidDirection.East -> {
                while (face.containsOnly(0) && face.lowerZ > lowerZ) {
                    face = face.shift(CuboidDirection.East, 1)
                }
                Cuboid(worldName, lowerX, lowerY, lowerZ, upperX, upperY, face.upperZ)
            }
            CuboidDirection.West -> {
                while (face.containsOnly(0) && face.upperZ < upperZ) {
                    face = face.shift(CuboidDirection.West, 1)
                }
                Cuboid(worldName, lowerX, lowerY, face.lowerZ, upperX, upperY, upperZ)
            }
            else -> throw IllegalArgumentException("Invalid direction $dir")
        }
    }

    /**
     * Get the Cuboid representing the face of this Cuboid.  The resulting Cuboid will be one block thick in the axis perpendicular to the requested face.
     *
     * @param dir - which face of the Cuboid to getInstance
     * @return The Cuboid representing this Cuboid's requested face
     */
    fun getFace(dir: CuboidDirection): Cuboid {
        return when (dir) {
            CuboidDirection.Down -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                upperX,
                lowerY,
                upperZ
            )
            CuboidDirection.Up -> Cuboid(
                worldName,
                lowerX,
                upperY,
                lowerZ,
                upperX,
                upperY,
                upperZ
            )
            CuboidDirection.North -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                lowerX,
                upperY,
                upperZ
            )
            CuboidDirection.South -> Cuboid(
                worldName,
                upperX,
                lowerY,
                lowerZ,
                upperX,
                upperY,
                upperZ
            )
            CuboidDirection.East -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                lowerZ,
                upperX,
                upperY,
                lowerZ
            )
            CuboidDirection.West -> Cuboid(
                worldName,
                lowerX,
                lowerY,
                upperZ,
                upperX,
                upperY,
                upperZ
            )
            else -> throw IllegalArgumentException("Invalid direction $dir")
        }
    }

    /**
     * Check if the Cuboid contains only blocks of the given type
     *
     * @param blockId - The block ID to check for
     * @return true if this Cuboid contains only blocks of the given type
     */
    fun containsOnly(blockId: Int): Boolean {
        for (b in this) {
            if (b.typeId != blockId) return false
        }
        return true
    }

    /**
     * Get the Cuboid big enough to hold both this Cuboid and the given one.
     *
     * @param other - The other cuboid.
     * @return A new Cuboid large enough to hold this Cuboid and the given Cuboid
     */
    fun getBoundingCuboid(other: Cuboid?): Cuboid {
        if (other == null) return this
        val xMin = lowerX.coerceAtMost(other.lowerX)
        val yMin = lowerY.coerceAtMost(other.lowerY)
        val zMin = lowerZ.coerceAtMost(other.lowerZ)
        val xMax = upperX.coerceAtLeast(other.upperX)
        val yMax = upperY.coerceAtLeast(other.upperY)
        val zMax = upperZ.coerceAtLeast(other.upperZ)
        return Cuboid(worldName, xMin, yMin, zMin, xMax, yMax, zMax)
    }

    /**
     * Get a block relative to the lower NE point of the Cuboid.
     *
     * @param x - The X co-ordinate
     * @param y - The Y co-ordinate
     * @param z - The Z co-ordinate
     * @return The block at the given position
     */
    fun getRelativeBlock(x: Int, y: Int, z: Int): Block {
        return world.getBlockAt(lowerX + x, lowerY + y, lowerZ + z)
    }

    /**
     * Get a block relative to the lower NE point of the Cuboid in the given World.  This
     * version of getRelativeBlock() should be used if being called many times, to avoid
     * excessive calls to getWorld().
     *
     * @param w - The world
     * @param x - The X co-ordinate
     * @param y - The Y co-ordinate
     * @param z - The Z co-ordinate
     * @return The block at the given position
     */
    fun getRelativeBlock(w: World, x: Int, y: Int, z: Int): Block {
        return w.getBlockAt(lowerX + x, lowerY + y, lowerZ + z)
    }

    /**
     * Get a list of the chunks which are fully or partially contained in this cuboid.
     *
     * @return A list of Chunk objects
     */
    val chunks: List<Chunk>
        get() {
            val res: MutableList<Chunk> = ArrayList()
            val w = world
            val x1 = lowerX and 0xf.inv()
            val x2 = upperX and 0xf.inv()
            val z1 = lowerZ and 0xf.inv()
            val z2 = upperZ and 0xf.inv()
            var x = x1
            while (x <= x2) {
                var z = z1
                while (z <= z2) {
                    res.add(w.getChunkAt(x shr 4, z shr 4))
                    z += 16
                }
                x += 16
            }
            return res
        }

    override fun iterator(): MutableIterator<Block> {
        return CuboidIterator(world, lowerX, lowerY, lowerZ, upperX, upperY, upperZ)
    }

    public override fun clone(): Cuboid {
        return Cuboid(this)
    }

    override fun toString(): String {
        return "$worldName:$lowerX:$lowerY:$lowerZ:$upperX:$upperY:$upperZ"
    }

    enum class CuboidDirection {
        North, East, South, West, Up, Down, Horizontal, Vertical, Both, Unknown;

        fun opposite(): CuboidDirection {
            return when (this) {
                North -> South
                East -> West
                South -> North
                West -> East
                Horizontal -> Vertical
                Vertical -> Horizontal
                Up -> Down
                Down -> Up
                Both -> Both
                else -> Unknown
            }
        }
    }

    inner class CuboidIterator(
        private val w: World,
        private val baseX: Int,
        private val baseY: Int,
        private val baseZ: Int,
        x2: Int,
        y2: Int,
        z2: Int
    ) : MutableIterator<Block> {
        private var x: Int
        private var y: Int
        private var z: Int
        private val sizeX: Int
        private val sizeY: Int
        private val sizeZ: Int
        override fun hasNext(): Boolean {
            return x < this.sizeX && y < this.sizeY && z < this.sizeZ
        }

        override fun next(): Block {
            val b = w.getBlockAt(baseX + x, baseY + y, baseZ + z)
            if (++x >= this.sizeX) {
                x = 0
                if (++y >= this.sizeY) {
                    y = 0
                    ++z
                }
            }
            return b
        }

        override fun remove() {}

        init {
            this.sizeX = abs(x2 - baseX) + 1
            this.sizeY = abs(y2 - baseY) + 1
            this.sizeZ = abs(z2 - baseZ) + 1
            z = 0
            y = z
            x = y
        }
    }

    companion object {
        fun fromString(string: String): Cuboid? {
            val split = string.split(":").toTypedArray()
            val world = Bukkit.getWorld(split[0]) ?: return null
            return Cuboid(
                world,
                split[1].toInt(),
                split[2].toInt(),
                split[3].toInt(),
                split[4].toInt(),
                split[5].toInt(),
                split[6].toInt()
            )
        }
    }
}