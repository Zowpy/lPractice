package net.lyragames.practice.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.PacketType.Play.Server.*
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.google.common.base.Preconditions
import com.google.common.collect.HashBasedTable
import lombok.SneakyThrows
import net.lyragames.practice.PracticePlugin
import net.minecraft.server.v1_8_R3.EntityItem
import net.minecraft.server.v1_8_R3.MathHelper
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.reflect.Field
import java.util.*


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/26/2022
 * Project: lPractice
 */

class EntityHider(plugin: PracticePlugin, policy: Policy) {
    var observerEntityMap: HashBasedTable<Any, Any, Any> = HashBasedTable.create<Any, Any, Any>()
    private var itemOwner: Field? = null
    private val ENTITY_PACKETS: MutableList<PacketType> = mutableListOf(
        ENTITY_EQUIPMENT, BED, ANIMATION, NAMED_ENTITY_SPAWN,
        COLLECT, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, SPAWN_ENTITY_PAINTING, SPAWN_ENTITY_EXPERIENCE_ORB,
        ENTITY_VELOCITY, REL_ENTITY_MOVE, ENTITY_LOOK, ENTITY_MOVE_LOOK, ENTITY_TELEPORT,
        ENTITY_HEAD_ROTATION, ENTITY_STATUS, ATTACH_ENTITY, ENTITY_METADATA,
        ENTITY_EFFECT, REMOVE_ENTITY_EFFECT, BLOCK_BREAK_ANIMATION,
        WORLD_EVENT,
        NAMED_SOUND_EFFECT
    )

    enum class Policy {
        /**
         * All entities are invisible by default. Only entities specifically made visible may be seen.
         */
        WHITELIST,

        /**
         * All entities are visible by default. An entity can only be hidden explicitly.
         */
        BLACKLIST
    }

    private val manager: ProtocolManager
    private val plugin: PracticePlugin
    private val bukkitListener: Listener
    private val protocolListener: PacketAdapter
    private val policy: Policy
    @SneakyThrows
    fun init() {
        manager.addPacketListener(protocolListener)
        plugin.getServer().getPluginManager().registerEvents(bukkitListener, plugin)
        itemOwner = EntityItem::class.java.getDeclaredField("f")
        itemOwner?.isAccessible = true
    }

    fun setVisibility(observer: Player, entityID: Int, visible: Boolean): Boolean {
        return when (policy) {
            Policy.BLACKLIST ->                 // Non-membership means they are visible
                !setMembership(observer, entityID, !visible)
            Policy.WHITELIST -> setMembership(observer, entityID, visible)
            else -> throw IllegalArgumentException("Unknown policy: $policy")
        }
    }

    /**
     * Add or remove the given entity and observer entry from the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @param member   - TRUE if they should be present in the table, FALSE otherwise.
     * @return TRUE if they already were present, FALSE otherwise.
     */
    // Helper method
    fun setMembership(observer: Player, entityID: Int, member: Boolean): Boolean {
        return if (member) {
            observerEntityMap.put(observer.entityId, entityID, true) != null
        } else {
            observerEntityMap.remove(observer.entityId, entityID) != null
        }
    }

    /**
     * Determine if the given entity and observer is present in the table.
     *
     * @param observer - the player observer.
     * @param entityID - ID of the entity.
     * @return TRUE if they are present, FALSE otherwise.
     */
    fun getMembership(observer: Player, entityID: Int): Boolean {
        return observerEntityMap.contains(observer.entityId, entityID)
    }

    /**
     * Determine if a given entity is visible for a particular observer.
     *
     * @param observer - the observer player.
     * @param entityID -  ID of the entity that we are testing for visibility.
     * @return TRUE if the entity is visible, FALSE otherwise.
     */
    fun isVisible(observer: Player, entityID: Int): Boolean {
        // If we are using a whitelist, presence means visibility - if not, the opposite is the case
        val presence = getMembership(observer, entityID)
        return policy == Policy.WHITELIST == presence
    }

    /**
     * Remove the given entity from the underlying map.
     *
     * @param entity - the entity to remove.
     */
    fun removeEntity(entity: Entity) {
        val entityID: Int = entity.entityId
        for (maps in observerEntityMap.rowMap().values) {
            maps.remove(entityID)
        }
    }

    /**
     * Invoked when a player logs out.
     *
     * @param player - the player that used logged out.
     */
    fun removePlayer(player: Player) {
        // Cleanup
        observerEntityMap.rowMap().remove(player.entityId)
    }

    /**
     * Construct the Bukkit event listener.
     *
     * @return Our listener.
     */
    private fun constructBukkit(): Listener {
        return object : Listener {
            @EventHandler
            fun onEntityDeath(e: EntityDeathEvent) {
                removeEntity(e.entity)
            }

            @EventHandler
            fun onChunkUnload(e: ChunkUnloadEvent) {
                for (entity in e.chunk.entities) {
                    removeEntity(entity)
                }
            }

            @EventHandler
            fun onPlayerQuit(e: PlayerQuitEvent) {
                removePlayer(e.player)
            }

            @EventHandler(priority = EventPriority.MONITOR)
            fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
                val receiver: Player = event.player
                val item: Item = event.item
                val dropper: Player = getPlayerWhoDropped(item) ?: return
                if (!receiver.canSee(dropper)) {
                    event.isCancelled = true
                }
            }

            @EventHandler(priority = EventPriority.MONITOR)
            fun onPickup(event: PlayerPickupItemEvent) {
                val receiver: Player = event.player
                val item: Item = event.item
                if (item.itemStack.type !== Material.ARROW) return
                val entity = (item as CraftEntity).handle.bukkitEntity as? Arrow ?: return
                val arrow = entity as Arrow
                if (arrow.shooter !is Player) return
                val shooter: Player = arrow.shooter as Player
                if (!receiver.canSee(shooter)) {
                    event.isCancelled = true
                }
            }

            @EventHandler(priority = EventPriority.MONITOR)
            fun onPotionSplash(event: PotionSplashEvent) {
                val potion = event.entity
                if (potion.shooter !is Player) return
                val shooter: Player = potion.shooter as Player
                for (livingEntity in event.affectedEntities) {
                    if (livingEntity !is Player) return
                    val receiver: Player = livingEntity as Player
                    if (!receiver.canSee(shooter)) {
                        event.setIntensity(receiver, 0.0)
                    }
                }
            }
        }
    }

    /**
     * Construct the packet listener that will be used to intercept every entity-related packet.
     *
     * @param plugin - the parent plugin.
     * @return The packet listener.
     */
    private fun constructProtocol(plugin: Plugin): PacketAdapter {
        return object : PacketAdapter(plugin, ENTITY_PACKETS) {
            override fun onPacketSending(event: PacketEvent) {
                val entityID: Int = event.getPacket().getIntegers().read(0)

                // See if this packet should be cancelled
                if (!isVisible(event.getPlayer(), entityID)) {
                    event.setCancelled(true)
                }
                val type = event.getPacketType()
                val receiver: Player = event.getPlayer()
                if (type === WORLD_EVENT) {
                    val effect: Int = event.getPacket().getIntegers().read(0)
                    if (effect != 2002) return
                    val position = event.getPacket().getBlockPositionModifier().read(0)
                    val x = position.x
                    val y = position.y
                    val z = position.z
                    var isVisible = false
                    var isInMatch = false
                    for (potion in receiver.getWorld().getEntitiesByClass(ThrownPotion::class.java)) {
                        val potionX: Int = MathHelper.floor(x.toDouble())
                        val potionY: Int = MathHelper.floor(y.toDouble())
                        val potionZ: Int = MathHelper.floor(z.toDouble())
                        if (potion.shooter !is Player) continue
                        if (x != potionX || y != potionY || z != potionZ) continue
                        isInMatch = true
                        val shooter: Player = potion.shooter as Player
                        if (receiver.canSee(shooter)) isVisible = true
                    }
                    if (isInMatch && !isVisible) {
                        event.setCancelled(true)
                    }
                } else if (type === NAMED_SOUND_EFFECT) {
                    val sound: String = event.getPacket().getStrings().read(0)
                    if (sound != "RANDOM.bow" && sound != "RANDOM.bowhit" && sound != "RANDOM.pop" && sound != "game.player.hurt") return
                    val x = event.packet.integers.read(0)
                    val y = event.packet.integers.read(1)
                    val z = event.packet.integers.read(2)
                    var isVisible = false
                    var isInMatch = false
                    for (entity in receiver.getWorld()
                        .getEntitiesByClasses(Player::class.java, Projectile::class.java)) {
                        if (entity !is Player && entity !is Projectile) continue
                        var player: Player? = null
                        val location: Location = entity.location
                        if (entity is Player) {
                            player = entity as Player
                        }
                        if (entity is Projectile) {
                            val projectile = entity as Projectile
                            if (projectile.shooter is Player) {
                                player = projectile.shooter as Player
                            }
                        }
                        if (player == null) continue
                        val one = location.x.toInt() * 8 == x
                        val two = location.y.toInt() * 8 == y
                        val three = location.z.toInt() * 8 == z
                        if (!one || !two || !three) continue
                        var pass = false
                        when (sound) {
                            "RANDOM.bow" -> {
                                val hand: ItemStack = player.itemInHand ?: break
                                if (hand.type === Material.POTION || hand.type === Material.BOW || hand.type === Material.ENDER_PEARL) {
                                    pass = true
                                }
                            }
                            "RANDOM.bowhit" -> {
                                run {
                                    if (entity is Arrow) {
                                        pass = true
                                    }
                                }
                                run {
                                    if (entity is Player) {
                                        pass = true
                                    }
                                }
                            }
                            else -> {
                                if (entity is Player) {
                                    pass = true
                                    break
                                }
                            }
                        }
                        if (pass) {
                            isInMatch = true
                            if (receiver.canSee(player)) isVisible = true
                        }
                    }
                    if (isInMatch && !isVisible) {
                        event.isCancelled = true
                    }
                } else {
                    val entity = (receiver.world as CraftWorld).handle.a(entityID)
                    if (entity is Player) {
                        val player: Player = entity as Player
                        if (receiver.canSee(player)) return
                        event.isCancelled = true
                    } else if (entity is Projectile) {
                        val projectile = entity as Projectile
                        if (projectile.shooter !is Player) return
                        val shooter: Player = projectile.shooter as Player
                        if (receiver.canSee(shooter)) return
                        event.isCancelled = true
                    } else if (entity is Item) {
                        val item: Item = entity as Item
                        val dropper: Player = getPlayerWhoDropped(item) ?: return
                        if (receiver.canSee(dropper)) return
                        event.isCancelled = true
                    }
                }
            }
        }
    }

    /**
     * Toggle the visibility status of an entity for a player.
     *
     *
     * If the entity is visible, it will be hidden. If it is hidden, it will become visible.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to toggle.
     * @return TRUE if the entity was visible before, FALSE otherwise.
     */
    fun toggleEntity(observer: Player, entity: Entity): Boolean {
        return if (isVisible(observer, entity.entityId)) {
            hideEntity(observer, entity)
        } else {
            !showEntity(observer, entity)
        }
    }

    /**
     * Allow the observer to see an entity that was previously hidden.
     *
     * @param observer - the observer.
     * @param entity   - the entity to show.
     * @return TRUE if the entity was hidden before, FALSE otherwise.
     */
    fun showEntity(observer: Player, entity: Entity): Boolean {
        validate(observer, entity)
        val hiddenBefore = !setVisibility(observer, entity.entityId, true)

        // Resend packets
        if (manager != null && hiddenBefore) {
            manager.updateEntity(entity, Collections.singletonList(observer))
        }
        return hiddenBefore
    }

    /**
     * Prevent the observer from seeing a given entity.
     *
     * @param observer - the player observer.
     * @param entity   - the entity to hide.
     * @return TRUE if the entity was previously visible, FALSE otherwise.
     */
    fun hideEntity(observer: Player, entity: Entity): Boolean {
        validate(observer, entity)
        val visibleBefore = setVisibility(observer, entity.entityId, false)
        if (visibleBefore) {
            // Make the entity disappear
            try {
                destroy(observer, entity.entityId)
            } catch (e: Exception) {
                throw RuntimeException("Cannot send server packet.", e)
            }
        }
        return visibleBefore
    }

    /**
     * Determine if the given entity has been hidden from an observer.
     * Note that the entity may very well be occluded or out of range from the perspective
     * of the observer. This method simply checks if an entity has been completely hidden
     * for that observer.
     *
     * @param observer - the observer.
     * @param entity   - the entity that may be hidden.
     * @return TRUE if the player may see the entity, FALSE if the entity has been hidden.
     */
    fun canSee(observer: Player, entity: Entity): Boolean {
        validate(observer, entity)
        return isVisible(observer, entity.entityId)
    }

    // For validating the input parameters
    private fun validate(observer: Player, entity: Entity) {
        Preconditions.checkNotNull(observer, "observer cannot be NULL.")
        Preconditions.checkNotNull(entity, "entity cannot be NULL.")
    }

    /**
     * Retrieve the current visibility policy.
     *
     * @return The current visibility policy.
     */
    fun getPolicy(): Policy {
        return policy
    }

    fun close() {
        HandlerList.unregisterAll(bukkitListener)
        manager.removePacketListener(protocolListener)
        itemOwner?.setAccessible(false)
    }

    private fun getPlayerWhoDropped(item: Item): Player? {
        return try {
            val name = itemOwner?.get((item as CraftEntity).handle) as String ?: return null
            Bukkit.getPlayer(name)
        } catch (e: Exception) {
            null
        }
    }

    fun destroy(player: Player, entityId: Int) {
        val packet = PacketPlayOutEntityDestroy(entityId)
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    init {
        Preconditions.checkNotNull(plugin, "plugin cannot be NULL.")
        this.plugin = plugin
        this.policy = policy
        manager = ProtocolLibrary.getProtocolManager()
        bukkitListener = constructBukkit()
        protocolListener = constructProtocol(plugin)
    }
}