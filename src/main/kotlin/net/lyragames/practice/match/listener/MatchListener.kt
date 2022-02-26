package net.lyragames.practice.match.listener

import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 1/27/2022
 * Project: lPractice
 */
object MatchListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match?.matchState != MatchState.FIGHTING) {
                event.isCancelled = true
                return
            }

            if (match.kit.kitData.build) {
                match.blocksPlaced.add(event.blockPlaced)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match?.matchState != MatchState.FIGHTING) {
                event.isCancelled = true
                return
            }

            if (match.kit.kitData.build && match.blocksPlaced.contains(event.block)) {
                match.blocksPlaced.remove(event.block)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onLiquidPlace(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            if (match!!.kit.kitData.build) {
                match.blocksPlaced.add(event.blockClicked)
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onLiquidFill(event: PlayerBucketFillEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.kit.kitData.build && match.blocksPlaced.contains(event.blockClicked)) {
                match.blocksPlaced.remove(event.blockClicked)
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.state == ProfileState.FFA) {
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            match!!.droppedItems.add(event.itemDrop)
        }else {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickup(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.state == ProfileState.FFA) {
            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.droppedItems.contains(event.item)) {
                match.droppedItems.remove(event.item)
            }else {
                event.isCancelled = true
            }
        }else {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onHit(event: EntityDamageByEntityEvent) {

        if (event.entity is Player) {
            val profile = Profile.getByUUID(event.entity.uniqueId)

            if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE || profile?.state == ProfileState.SPECTATING) {
                event.isCancelled = true
                return
            }
        }

        if (event.entity is Player && event.damager is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player

            val profile = Profile.getByUUID(player.uniqueId)
            val profile1 = Profile.getByUUID(damager.uniqueId)

            if (profile?.state == ProfileState.FFA && profile1?.state == ProfileState.FFA) {

                if (profile.ffa != profile1.ffa) {
                    event.isCancelled = true
                    return
                }

                val ffa = FFAManager.getByUUID(profile.ffa!!)

                val ffaPlayer = ffa?.getFFAPlayer(profile.uuid)
                val ffaPlayer1 = ffa?.getFFAPlayer(profile1.uuid)

                if (event.finalDamage >= player.health) {
                    ffa?.handleDeath(ffaPlayer!!, ffaPlayer1!!)
                }

                return
            }

            if (profile?.state != ProfileState.MATCH || profile1?.state != ProfileState.MATCH) {
                event.isCancelled = true
                return
            }

            if (profile.match?.equals(profile1.match)!!) {
                val match = Match.getByUUID(profile.match!!)

                if (match?.matchState != MatchState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                val matchPlayer = match.getMatchPlayer(player.uniqueId)
                val matchPlayer1 = match.getMatchPlayer(damager.uniqueId)

                if (!match.canHit(player, damager)) {
                    event.isCancelled = true
                }else {
                    matchPlayer?.lastDamager = damager.uniqueId
                    matchPlayer1!!.hits++
                    matchPlayer1.combo++

                    matchPlayer?.combo = 0
                }

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        match.handleDeath(matchPlayer)
                    }
                }
            }
        }else if (event.entity is Player && event.damager == null || event.damager !is Player) {
            val player = event.entity as Player
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.state == ProfileState.FFA) {
                return
            }

            if (profile?.match != null) {
                val match = Match.getByUUID(profile.match!!)

                if (match?.matchState != MatchState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                val matchPlayer = match.getMatchPlayer(player.uniqueId)
                matchPlayer?.lastDamager = null

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        match.handleDeath(matchPlayer)
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        if (event.entity.shooter is Player) {
            val shooter = event.entity.shooter as Player
            val profile = Profile.getByUUID(shooter.uniqueId)

            if (profile?.state == ProfileState.FFA) {
                return
            }

            if (profile?.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)

                if (match?.matchState == MatchState.STARTING) {
                    event.isCancelled = true
                } else if (match?.matchState == MatchState.FIGHTING) {
                    if (event.entity is ThrownPotion) {
                        match.getMatchPlayer(shooter.uniqueId)!!.potionsThrown++
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPotionSplashEvent(event: PotionSplashEvent) {
        if (event.potion.shooter is Player) {
            val shooter = event.potion.shooter as Player
            val shooterData = Profile.getByUUID(shooter.uniqueId)

            if (shooterData?.state == ProfileState.FFA) {
                return
            }

            if (shooterData?.state == ProfileState.MATCH &&
                Match.getByUUID(shooterData.match!!)?.matchState == MatchState.FIGHTING
            ) {
                val match = Match.getByUUID(shooterData.match!!)

                if (event.getIntensity(shooter) <= 0.5) {
                    match?.getMatchPlayer(shooter.uniqueId)!!.potionsMissed++
                }
                for (entity in event.affectedEntities) {
                    if (entity is Player) {
                        if (match?.getMatchPlayer(entity.uniqueId) == null) {
                            event.setIntensity(entity as LivingEntity, 0.0)
                        }
                    }
                }
            }
        }
    }
}