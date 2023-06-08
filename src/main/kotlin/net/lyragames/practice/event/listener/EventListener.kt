package net.lyragames.practice.event.listener

import net.lyragames.llib.utils.CC
import net.lyragames.practice.event.Event
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.event.impl.BracketsEvent
import net.lyragames.practice.event.player.EventPlayerState
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

object EventListener : Listener {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {
                if (currentEvent.type == EventType.BRACKETS) {
                    val bracketEvent = currentEvent as BracketsEvent

                    if (bracketEvent.kit.kitData.build && bracketEvent.state == EventState.FIGHTING) {
                        bracketEvent.blocksPlaced.add(event.blockPlaced)
                    }else {
                        event.isCancelled = true
                    }
                }else {
                    event.isCancelled = true
                }
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {
                if (currentEvent.type == EventType.BRACKETS) {
                    val bracketEvent = currentEvent as BracketsEvent

                    if (bracketEvent.kit.kitData.build && bracketEvent.state == EventState.FIGHTING && bracketEvent.blocksPlaced.contains(event.block)) {
                        bracketEvent.blocksPlaced.remove(event.block)
                    }else {
                        event.isCancelled = true
                    }
                }else {
                    event.isCancelled = true
                }
            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onLiquidPlace(event: PlayerBucketEmptyEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {
                if (currentEvent.type == EventType.BRACKETS) {
                    val bracketEvent = currentEvent as BracketsEvent

                    if (bracketEvent.kit.kitData.build && bracketEvent.state == EventState.FIGHTING) {
                        bracketEvent.blocksPlaced.add(event.blockClicked)
                    }else {
                        event.isCancelled = true
                    }
                }else {
                    event.isCancelled = true
                }
            }else {
                event.isCancelled = true
            }

            return
        }
    }

    @EventHandler
    fun onLiquidFill(event: PlayerBucketFillEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {
                if (currentEvent.type == EventType.BRACKETS) {
                    val bracketEvent = currentEvent as BracketsEvent

                    if (bracketEvent.kit.kitData.build && bracketEvent.state == EventState.FIGHTING && bracketEvent.blocksPlaced.contains(event.blockClicked)) {
                        bracketEvent.blocksPlaced.remove(event.blockClicked)
                    }else {
                        event.isCancelled = true
                    }
                }else {
                    event.isCancelled = true
                }
            }else {
                event.isCancelled = true
            }

            return
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {

                if (currentEvent.state == EventState.FIGHTING) {
                    currentEvent.droppedItems.add(event.itemDrop)
                }else {
                    event.isCancelled = true
                }

            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPickup(event: PlayerPickupItemEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {
            val currentEvent = EventManager.event
            val eventPlayer = currentEvent?.getPlayer(player.uniqueId)

            if (eventPlayer?.state == EventPlayerState.FIGHTING) {

                if (currentEvent.state == EventState.FIGHTING && currentEvent.droppedItems.contains(event.item)) {
                    currentEvent.droppedItems.remove(event.item)
                }else {
                    event.isCancelled = true
                }

            }else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {

        if (event.entity is Player && event.damager is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player

            val profile = Profile.getByUUID(player.uniqueId)
            val profile1 = Profile.getByUUID(damager.uniqueId)

            if (profile?.state == ProfileState.EVENT && profile1?.state == ProfileState.EVENT) {

                val currentEvent = EventManager.event

                if (currentEvent == null) {
                    event.isCancelled = true
                    return
                }

                if (currentEvent.state != EventState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                if (currentEvent.type == EventType.TNT_RUN) {
                    event.isCancelled = true
                    return
                }

                if (currentEvent.type == EventType.SUMO) {
                    event.damage = 0.0
                }

                val eventPlayer = currentEvent.getPlayer(player.uniqueId)
                val eventPlayer1 = currentEvent.getPlayer(damager.uniqueId)

                if (currentEvent.playingPlayers.stream().noneMatch { it.uuid == eventPlayer?.uuid }
                    && currentEvent.playingPlayers.stream().noneMatch { it.uuid == eventPlayer1?.uuid }) {
                    event.isCancelled = true
                    return
                }

                if (currentEvent.type == EventType.TNT_TAG) {

                    event.damage = 0.0

                    if (eventPlayer1?.tagged!!) {

                        eventPlayer?.tagged = true
                        eventPlayer?.player?.inventory?.helmet = ItemStack(Material.TNT)
                        eventPlayer1.player.inventory.helmet = null

                        eventPlayer1.player.inventory.clear()
                        eventPlayer1.tagged = false

                        for (x in 0 until 35) {
                            eventPlayer?.player?.inventory?.setItem(x, ItemStack(Material.TNT))
                        }

                        eventPlayer?.player?.updateInventory()

                        currentEvent.sendMessage("${CC.SECONDARY}${eventPlayer?.player?.name}${CC.PRIMARY} is the tagger!")
                    }
                }

                if (!currentEvent.canHit(player, damager)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity as Player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile!!.state != ProfileState.EVENT) return

        val currentEvent = EventManager.event ?: return

        val eventPlayer = currentEvent.getPlayer(player.uniqueId)

        if (!currentEvent.getAlivePlayers().contains(eventPlayer)) return

        eventPlayer!!.dead = true
        currentEvent.endRound(currentEvent.getOpponent(eventPlayer))
    }

    @EventHandler
    fun onHunger(event: FoodLevelChangeEvent) {
        val profile = Profile.getByUUID(event.entity.uniqueId)

        if (profile?.state == ProfileState.EVENT && EventManager.event != null) {
            val currentEvent = EventManager.event

            if (currentEvent?.type != EventType.BRACKETS) {
                event.isCancelled = true
                return
            }

            if (!(currentEvent as BracketsEvent).kit.kitData.hunger) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val profile = Profile.getByUUID((event.entity as Player).player.uniqueId)

            if (profile!!.state == ProfileState.EVENT) {
                val currentEvent = EventManager.event

                if (currentEvent!!.state != EventState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                if (currentEvent.type == EventType.BRACKETS) {
                    val bracketEvent = currentEvent as BracketsEvent

                    event.isCancelled = !(bracketEvent.kit.kitData.fallDamage && bracketEvent.isPlaying(profile.uuid))
                }
            }
        }
    }

    @EventHandler
    fun onPotionSplash(event: PotionSplashEvent) {
        if (event.potion.shooter is Player) {
            val shooterData = Profile.getByUUID((event.potion.shooter as Player).uniqueId)

            if (shooterData?.state == ProfileState.EVENT) {
                val currentEvent = EventManager.event
                val eventPlayer = currentEvent?.getPlayer(shooterData.uuid)

                if (eventPlayer?.state != EventPlayerState.FIGHTING) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.EVENT) {

            val currentEvent = EventManager.event ?: return
            val eventPlayer = currentEvent.getPlayer(player.uniqueId)

            if (currentEvent.playingPlayers.stream().noneMatch { it.uuid == player.uniqueId }) return

            eventPlayer?.dead = true
            eventPlayer?.offline = true

            currentEvent.endRound(currentEvent.getOpponent(eventPlayer!!))
        }
    }
}