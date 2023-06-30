package net.lyragames.practice.match.listener

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cooldown
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.impl.*
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.Potion

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 1/27/2022
 * Project: lPractice
 */

object MatchListener : Listener {

    // TODO: Clean this class.

    @EventHandler(ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)

            if (match?.matchState != MatchState.FIGHTING) {
                event.isCancelled = true
                return
            }

            val matchPlayer = match.getMatchPlayer(player.uniqueId)

            if (matchPlayer?.dead!! || matchPlayer.respawning) {
                event.isCancelled = true
                return
            }

            if (match.kit.kitData.build || match.kit.kitData.mlgRush || match.kit.kitData.bedFights || match.kit.kitData.bridge || match.kit.kitData.fireballFight) {
                if (!match.arena.bounds.isInCuboid(event.blockPlaced.location)) {
                    event.isCancelled = true
                    player.sendMessage("${CC.RED}You cannot place blocks here.")
                    return
                }

                if (match.kit.kitData.bridge) {
                    (match as BridgeMatch).handlePlace(event)
                    return
                }

                if (event.block.type == Material.TNT) {
                    event.block.type = Material.AIR

                    val tnt = player.location.world.spawn(event.block.location, TNTPrimed::class.java) as TNTPrimed
                    tnt.fuseTicks = 4 * 20
                    tnt.setMetadata("match", FixedMetadataValue(PracticePlugin.instance, match.uuid.toString()))
                    return
                }

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

        if (profile!!.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.matchState != MatchState.FIGHTING) {
                event.isCancelled = true
                return
            }

            if (match.kit.kitData.mlgRush && match is MLGRushMatch) {
                match.handleBreak(event)
                return
            }

            if (match.kit.kitData.bedFights && match is BedFightMatch) {
                match.handleBreak(event)
                return
            }

            if (match.kit.kitData.fireballFight && match is FireballFightMatch) {
                match.handleBreak(event)
                return
            }

            if ((match.kit.kitData.build || match.kit.kitData.bridge) && match.blocksPlaced.contains(event.block)) {
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

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.kit.kitData.build) {
                match.blocksPlaced.add(event.blockClicked)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onLiquidFill(event: PlayerBucketFillEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)

            if (match!!.kit.kitData.build && match.blocksPlaced.contains(event.blockClicked)) {
                match.blocksPlaced.remove(event.blockClicked)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)
            val matchPlayer = match?.getMatchPlayer(player.uniqueId)

            if (matchPlayer?.dead!! || matchPlayer.respawning) {
                event.isCancelled = true
                return
            }

            match.droppedItems.add(event.itemDrop)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPickup(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)
            val matchPlayer = match?.getMatchPlayer(player.uniqueId)

            if (matchPlayer?.dead!! || matchPlayer.respawning) {
                event.isCancelled = true
                return
            }

            if (match.droppedItems.contains(event.item)) {
                match.droppedItems.remove(event.item)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onHit(event: EntityDamageByEntityEvent) {
        if (event.damager is Explosive) {
            event.damage = 0.0
            return
        }

        if (event.entity is Player && event.damager is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player

            val profile = Profile.getByUUID(player.uniqueId)
            val profile1 = Profile.getByUUID(damager.uniqueId)

            if (profile!!.state == ProfileState.SPECTATING || profile1!!.state == ProfileState.SPECTATING) {
                event.isCancelled = true
                return
            }

            if (profile.state != ProfileState.MATCH || profile1.state != ProfileState.MATCH) {
                return
            }

            if (profile.match?.equals(profile1.match)!!) {
                val match = Match.getByUUID(profile.match!!)

                val matchPlayer = match!!.getMatchPlayer(player.uniqueId)
                val matchPlayer1 = match.getMatchPlayer(damager.uniqueId)

                if (!match.canHit(player, damager)) {
                    event.isCancelled = true
                } else {
                    if (matchPlayer!!.dead || matchPlayer1!!.dead || matchPlayer.respawning || matchPlayer1.respawning) {
                        event.isCancelled = true
                        return
                    }

                    matchPlayer.lastDamager = damager.uniqueId
                    matchPlayer.comboed++

                    matchPlayer1.hits++
                    matchPlayer1.combo++
                    matchPlayer1.comboed = 0

                    if (matchPlayer1.combo > matchPlayer1.longestCombo) {
                        matchPlayer1.longestCombo = matchPlayer1.combo
                    }

                    if (match.kit.kitData.boxing) {
                        event.damage = 0.0

                        if (match is TeamMatch) {
                            val team = match.getTeam((matchPlayer1 as TeamMatchPlayer).teamUniqueId)

                            team!!.hits++

                            if (team.hits >= 200) {
                                match.end(team)
                                return
                            }
                        }

                        if (matchPlayer1.hits >= 100) {
                            match.handleDeath(matchPlayer)
                        }
                    }

                    if (match.kit.kitData.mlgRush) {
                        event.damage = 0.0
                    }

                    matchPlayer.combo = 0
                }
            } else {
                event.isCancelled = true
            }
        } else if (event.entity is Player && event.damager == null || event.damager !is Player) {
            val player = event.entity as Player
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)
                val matchPlayer = match!!.getMatchPlayer(player.uniqueId)

                if (matchPlayer?.dead!! || matchPlayer.respawning) {
                    event.isCancelled = true
                    return
                }

                matchPlayer.lastDamager = null

                if (event.finalDamage >= player.health) {
                    match.handleDeath(matchPlayer)
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile!!.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!) ?: return

            match.handleDeath(match.getMatchPlayer(player.uniqueId)!!)
        }

        event.deathMessage = null
        event.keepInventory = true
        player.spigot().respawn()
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (Constants.SPAWN != null) {
            event.respawnLocation = Constants.SPAWN
        } else {
            event.respawnLocation = event.player.location
        }
    }

    @EventHandler
    fun onHunger(event: FoodLevelChangeEvent) {
        val profile = Profile.getByUUID(event.entity.uniqueId)

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!) ?: return

            if (!match.kit.kitData.hunger) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {

            if (event.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                event.damage = 0.0
                return
            }

            val profile = Profile.getByUUID((event.entity as Player).player.uniqueId)

            if (profile!!.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)
                val kit = match!!.kit

                if (match.matchState != MatchState.FIGHTING) {
                    event.isCancelled = true
                }

                val matchPlayer = match.getMatchPlayer(profile.uuid)

                if (matchPlayer!!.dead || matchPlayer.respawning) {
                    event.isCancelled = true
                    return
                }

                if (!kit.kitData.fallDamage && event.cause == EntityDamageEvent.DamageCause.FALL) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onRegen(event: EntityRegainHealthEvent) {
        if (event.entity is Player) {
            val profile = Profile.getByUUID((event.entity as Player).player.uniqueId)

            if (profile!!.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)
                val kit = match!!.kit

                if (!kit.kitData.regeneration && event.regainReason == EntityRegainHealthEvent.RegainReason.REGEN) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent) {
        if (event.entity.shooter is Player) {
            val shooter = event.entity.shooter as Player
            val profile = Profile.getByUUID(shooter.uniqueId)

            if (profile?.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)

                if (match?.matchState != MatchState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                if (event.entity is ThrownPotion) {
                    match.getMatchPlayer(shooter.uniqueId)!!.potionsThrown++
                }

                if (event.entity is Arrow) {
                    if (match.matchState != MatchState.FIGHTING) {
                        event.isCancelled = true
                        shooter.updateInventory()
                        return
                    }

                    if (match.kit.kitData.bridge) {
                        profile.arrowCooldown = Cooldown(PracticePlugin.instance, 5) {
                            if (shooter.inventory.getItem(8) == null || shooter.inventory.getItem(8).type == Material.AIR) {
                                shooter.inventory.setItem(8, ItemStack(Material.ARROW))
                            } else {
                                shooter.inventory.addItem(ItemStack(Material.ARROW))
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (event.action == Action.PHYSICAL && event.clickedBlock.type == Material.SOIL)
            event.isCancelled = true

        if (event.action.name.contains("RIGHT")) {

            if (event.hasBlock() && player.gameMode != GameMode.CREATIVE) {
                if (event.clickedBlock.type == Material.CHEST || event.clickedBlock.type == Material.FURNACE
                    || event.clickedBlock.type == Material.TRAPPED_CHEST || event.clickedBlock.type.name.contains("FENCE_GATE")
                    || event.clickedBlock.type.name.contains("DOOR") || event.clickedBlock.type == Material.WORKBENCH
                    || event.clickedBlock.type == Material.ITEM_FRAME
                ) {
                    event.isCancelled = true
                }
            }

            if (profile?.state != ProfileState.SPECTATING && profile?.state != ProfileState.QUEUE && profile?.state != ProfileState.LOBBY) {
                if (profile!!.state == ProfileState.MATCH && event.hasItem()) {
                    val match = Match.getByUUID(profile.match!!)

                    if (event.item.type == Material.BOW ||
                        (event.item.type == Material.POTION && Potion.fromItemStack(event.item).isSplash)
                        || event.item.type == Material.FIREBALL) {
                        if (match!!.matchState != MatchState.FIGHTING) {
                            event.isCancelled = true
                            event.setUseItemInHand(Event.Result.DENY)

                            player.updateInventory()
                            return
                        }

                        if (event.item.type == Material.FIREBALL) {

                            event.isCancelled = true

                            if (profile.fireBallCooldown != null && !profile.fireBallCooldown!!.hasExpired()) {
                                player.sendMessage("${CC.SECONDARY}Fireball cooldown: ${CC.PRIMARY}${TimeUtil.millisToSeconds(profile.fireBallCooldown!!.timeRemaining)}")
                            }else {
                                val fireBall = player.launchProjectile(Fireball::class.java)
                                fireBall.velocity = player.location.direction.multiply(1.4)
                                fireBall.setIsIncendiary(false)
                                fireBall.setMetadata("match", FixedMetadataValue(PracticePlugin.instance, match.uuid.toString()))

                                if (player.itemInHand.amount - 1 <= 0) {
                                    player.inventory.removeItem(player.itemInHand)
                                }else {

                                    player.itemInHand.amount = player.itemInHand.amount - 1
                                    player.updateInventory()
                                }

                                profile.fireBallCooldown = Cooldown(PracticePlugin.instance, 1) {}
                            }

                        }
                    }
                }

                if (event.hasItem() && event.item.type == Material.ENDER_PEARL) {
                    if (profile.enderPearlCooldown == null || profile.enderPearlCooldown?.hasExpired()!!) {
                        event.setUseItemInHand(Event.Result.ALLOW)

                        profile.enderPearlCooldown = Cooldown(PracticePlugin.instance, 16) {
                            player.sendMessage("${CC.PRIMARY}You can now use the enderpearl.")
                            profile.enderPearlCooldown = null
                        }
                    } else {
                        event.isCancelled = true
                        event.setUseItemInHand(Event.Result.DENY)

                        player.sendMessage(
                            "${CC.SECONDARY}Enderpearl cooldown: ${CC.PRIMARY}${
                                profile.enderPearlCooldown?.timeRemaining?.let {
                                    TimeUtil.millisToSeconds(it)
                                }
                            }"
                        )
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

    @EventHandler
    fun onPortal(event: EntityPortalEnterEvent) {
        if (event.entity is Player) {
            val profile = Profile.getByUUID((event.entity as Player).uniqueId)

            if (profile!!.state != ProfileState.MATCH) {
                return
            }

            val match = Match.getByUUID(profile.match!!)

            if (match!!.kit.kitData.bridge) {
                (match as BridgeMatch).handlePortal(event)
            }
        }
    }
}