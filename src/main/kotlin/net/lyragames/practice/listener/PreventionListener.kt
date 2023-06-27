package net.lyragames.practice.listener

import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*

object PreventionListener: Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile!!.state == ProfileState.SPECTATING) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onSleep(event: PlayerBedEnterEvent) {
        event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val profile = Profile.getByUUID((event.entity as Player).player.uniqueId)

            if (profile!!.state == ProfileState.LOBBY || profile.state == ProfileState.QUEUE || profile.state == ProfileState.SPECTATING) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onHunger(event: FoodLevelChangeEvent) {
        val profile = Profile.getByUUID(event.entity.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.SPECTATING || profile?.state == ProfileState.QUEUE) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onSpawn(event: EntitySpawnEvent) {
        /*if (event.entity.type == EntityType.PLAYER
            || event.entity.type == EntityType.PAINTING
            || event.entity.type == EntityType.ITEM_FRAME
            || event.entity.type == EntityType.DROPPED_ITEM
            || event.entity.type == EntityType.ARMOR_STAND
            || event.entity.type == EntityType.FAKE_PLAYER
            || event.entity.type == EntityType.FISHING_HOOK
            || event.entity.type == EntityType.) return */

        if (event.entity.type == EntityType.PLAYER || !event.entity.type.isAlive
            || !event.entity.type.isSpawnable) return

        event.isCancelled = true
        event.entity.remove()
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val profile = Profile.getByUUID(event.entity.uniqueId)

            if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickup(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onLiquidFill(event: PlayerBucketFillEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING || profile?.state == ProfileState.FFA) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onLiquidPlace(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING || profile?.state == ProfileState.FFA) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
        }

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE) {
            if (!profile.canBuild) {
                event.isCancelled = true
            }
        }

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
        }

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE) {
            if (player.gameMode != GameMode.CREATIVE && profile.canBuild) {
                event.isCancelled = true
            }
        }

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
        }
    }
}