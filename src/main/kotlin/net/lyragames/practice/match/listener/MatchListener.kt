package net.lyragames.practice.match.listener

import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Cooldown
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.event.EventState
import net.lyragames.practice.event.EventType
import net.lyragames.practice.event.impl.BracketsEvent
import net.lyragames.practice.event.player.EventPlayerState
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.impl.BedFightMatch
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

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

        if (profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE) {
            if (player.gameMode != GameMode.CREATIVE) {
                event.isCancelled = true
            }
            return
        }

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
            return
        }

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

            return
        }

        if (profile?.match != null) {
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


            if (match.kit.kitData.build || match.kit.kitData.mlgRush || match.kit.kitData.bedFights) {
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

        if (profile?.state == ProfileState.SPECTATING) {
            event.isCancelled = true
            return
        }

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.QUEUE) {
            if (player.gameMode != GameMode.CREATIVE) {
                event.isCancelled = true
            }
            return
        }

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
            return
        }

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

            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match?.matchState != MatchState.FIGHTING) {
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

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
            return
        }

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

        if (profile?.state == ProfileState.FFA) {
            event.isCancelled = true
            return
        }

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
            val ffa = FFAManager.getByUUID(profile.ffa!!) ?: return

            ffa.droppedItems.add(event.itemDrop)

            return
        }

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

            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            val matchPlayer = match?.getMatchPlayer(player.uniqueId)

            if (matchPlayer?.dead!! || matchPlayer.respawning) {
                event.isCancelled = true
                return
            }


            match.droppedItems.add(event.itemDrop)
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
            val ffa = FFAManager.getByUUID(profile.ffa!!) ?: return

            if (ffa.droppedItems.contains(event.item)) {
                ffa.droppedItems.remove(event.item)
            }else {
                event.isCancelled = true
            }

            return
        }

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

            return
        }

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)
            val matchPlayer = match?.getMatchPlayer(player.uniqueId)

            if (matchPlayer?.dead!! || matchPlayer.respawning) {
                event.isCancelled = true
                return
            }

            if (match.droppedItems.contains(event.item)) {
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

                    return
                }

                if (!currentEvent.canHit(player, damager)) {
                    event.isCancelled = true
                    return
                }

                if (event.finalDamage >= player.health) {
                    eventPlayer?.dead = true
                    currentEvent.endRound(eventPlayer1)
                }

                return
            }

            if (profile?.state == ProfileState.FFA && profile1?.state == ProfileState.FFA) {

                if (profile.ffa != profile1.ffa) {
                    event.isCancelled = true
                    return
                }

                val ffa = FFAManager.getByUUID(profile.ffa!!)

                val ffaPlayer = ffa?.getFFAPlayer(profile.uuid)
                val ffaPlayer1 = ffa?.getFFAPlayer(profile1.uuid)

                if (Constants.SAFE_ZONE != null && Constants.SAFE_ZONE!!.l1 != null && Constants.SAFE_ZONE!!.l2 != null) {
                    if (Constants.SAFE_ZONE!!.contains(player.location) || Constants.SAFE_ZONE!!.contains(damager.location)) {
                        event.isCancelled = true
                    }
                }

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
                    matchPlayer!!.comboed++

                    matchPlayer1!!.hits++
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
                        }

                        if (matchPlayer1.hits >= 100) {
                            match.handleDeath(matchPlayer)
                        }
                    }

                    if (match.kit.kitData.mlgRush) {
                        event.damage = 0.0
                    }

                    if (matchPlayer.dead || matchPlayer1.dead || matchPlayer.respawning || matchPlayer1.respawning) {
                        event.isCancelled = true
                        return
                    }

                    matchPlayer.combo = 0
                }

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        player.health = 0.0
                        match.handleDeath(matchPlayer)
                    }
                }
            }
        }else if (event.entity is Player && event.damager == null || event.damager !is Player) {
            val player = event.entity as Player
            val profile = Profile.getByUUID(player.uniqueId)

            if (profile?.state == ProfileState.FFA || profile?.state == ProfileState.EVENT) {
                return
            }

            if (profile?.match != null) {
                val match = Match.getByUUID(profile.match!!)

                if (match?.matchState != MatchState.FIGHTING) {
                    event.isCancelled = true
                    return
                }

                val matchPlayer = match.getMatchPlayer(player.uniqueId)

                if (match is MLGRushMatch && (matchPlayer?.dead!! || matchPlayer.respawning)) {
                    event.isCancelled = true
                    return
                }

                matchPlayer?.lastDamager = null

                if (event.finalDamage >= player.health) {
                    if (matchPlayer != null) {
                        match.handleDeath(matchPlayer)
                        event.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        event.deathMessage = null
        event.keepInventory = true
        event.entity.spigot().respawn()
    }

    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        if (event.entity.type == EntityType.PLAYER) return

        event.isCancelled = true
        event.entity.remove()
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (Constants.SPAWN != null) {
            event.respawnLocation = Constants.SPAWN
        }else {
            event.respawnLocation = event.player.location
        }
    }

    @EventHandler
    fun onHunger(event: FoodLevelChangeEvent) {
        val profile = Profile.getByUUID(event.entity.uniqueId)

        if (profile?.state == ProfileState.LOBBY || profile?.state == ProfileState.SPECTATING || profile?.state == ProfileState.QUEUE) {
            event.isCancelled = true
        }

        if (profile?.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!) ?: return

            if (!match.kit.kitData.hunger) {
                event.isCancelled = true
            }

            return
        }

        if (profile?.state == ProfileState.FFA) {
            val ffaMatch = FFAManager.getByUUID(profile.ffa!!) ?: return

            if (!ffaMatch.kit.kitData.hunger) {
                event.isCancelled = true
            }

            return
        }

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

            if (event.cause == EntityDamageEvent.DamageCause.FALL) {
                event.isCancelled = true
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

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (event.action == Action.PHYSICAL && event.clickedBlock.type == Material.SOIL)
            event.isCancelled = true

        if (event.action.name.contains("RIGHT")) {

            if (event.hasBlock() && profile?.state != ProfileState.LOBBY && player.gameMode != GameMode.CREATIVE) {
                if (event.clickedBlock.type == Material.CHEST || event.clickedBlock.type == Material.FURNACE
                    || event.clickedBlock.type.name.contains("DOOR")) {
                    event.isCancelled = true
                }
            }

            if (profile?.state != ProfileState.SPECTATING && profile?.state != ProfileState.QUEUE && profile?.state != ProfileState.LOBBY) {
                if (player.itemInHand != null && player.itemInHand.type == Material.ENDER_PEARL) {
                    if (profile!!.enderPearlCooldown == null || profile.enderPearlCooldown?.hasExpired()!!) {
                        event.setUseItemInHand(Event.Result.ALLOW)
                        profile.enderPearlCooldown = Cooldown(PracticePlugin.instance, 16) {
                            player.sendMessage("${CC.PRIMARY}You can now use the enderpearl.")
                            profile.enderPearlCooldown = null
                        }
                    } else {
                        event.isCancelled = true
                        event.setUseItemInHand(Event.Result.DENY)
                        player.sendMessage("${CC.RED}Enderpearl cooldown: ${CC.YELLOW}${profile.enderPearlCooldown?.timeRemaining?.let {
                            TimeUtil.millisToSeconds(
                                it
                            )
                        }}")
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

            if (shooterData?.state == ProfileState.EVENT) {
                val currentEvent = EventManager.event
                val eventPlayer = currentEvent?.getPlayer(shooter.uniqueId)

                if (eventPlayer?.state != EventPlayerState.FIGHTING) {
                    event.isCancelled = true
                }
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
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.state == ProfileState.FFA) {

            if (profile.ffa != null) {
                val ffa = FFAManager.getByUUID(profile.ffa!!)

                ffa?.players?.removeIf { it.uuid == player.uniqueId }
                profile.state = ProfileState.LOBBY
                profile.ffa = null
            }
        }

        if (profile?.state == ProfileState.QUEUE) {

            if (profile.queuePlayer != null) {

                val queue = QueueManager.getQueue(player.uniqueId)
                queue?.queuePlayers?.remove(profile.queuePlayer)

                profile.state = ProfileState.LOBBY
                profile.queuePlayer = null
            }

        }

        if (profile?.state == ProfileState.MATCH) {

            if (profile.match != null) {

                val match = Match.getByUUID(profile.match!!)

                match?.handleQuit(match.getMatchPlayer(player.uniqueId)!!)
            }
        }

        if (profile?.state == ProfileState.SPECTATING) {

            if (profile.spectatingMatch != null) {

                val match = Match.getByUUID(profile.spectatingMatch!!)

                match?.removeSpectator(player)
            }
        }

        if (profile?.state == ProfileState.EVENT) {

            val currentEvent = EventManager.event ?: return
            val eventPlayer = currentEvent.getPlayer(player.uniqueId)

            if (currentEvent.playingPlayers.stream().noneMatch { it.uuid == player.uniqueId }) return

            eventPlayer?.dead = true
            eventPlayer?.offline = true

            currentEvent.endRound(currentEvent.getOpponent(eventPlayer!!))
        }

        CustomItemStack.getCustomItemStacks().removeIf { it.uuid == player.uniqueId }
        Profile.profiles.remove(profile)
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        val profile = Profile.getByUUID(player.uniqueId)

        if (profile?.match != null) {
            val match = Match.getByUUID(profile.match!!)

            if (match != null) {

                if (match.kit.kitData.mlgRush || match.kit.kitData.bedFights) {

                    if (event.to.y <= match.arena.deadzone) {
                        val matchPlayer = match.getMatchPlayer(player.uniqueId)

                        if (!matchPlayer?.dead!!) {
                            match.handleDeath(matchPlayer)
                        }
                    }
                }
            }
        }

        if (event.to.block.type == Material.WATER || event.to.block.type == Material.STATIONARY_WATER) {

            if (profile?.match != null) {

                val match = Match.getByUUID(profile.match!!)

                if (match?.kit?.kitData?.sumo!!) {
                    if (event.to.block.type == Material.WATER || event.to.block.type == Material.STATIONARY_WATER) {
                        match.handleDeath(match.getMatchPlayer(player.uniqueId)!!)
                    }
                }



            } else if (profile?.state == ProfileState.EVENT) {

                val currentEvent = EventManager.event ?: return
                if (currentEvent.state != EventState.FIGHTING) return

                if (currentEvent.type == EventType.SUMO) {

                    val eventPlayer = currentEvent.getPlayer(player.uniqueId)

                    if (currentEvent.playingPlayers.stream().noneMatch { it.uuid == player.uniqueId }) return

                    eventPlayer?.dead = true

                    currentEvent.endRound(currentEvent.getOpponent(eventPlayer!!))
                }
            }
        }

    }

    @EventHandler
    fun onBlockDamage(event: BlockDamageEvent) {
        event.isCancelled = true
    }
}