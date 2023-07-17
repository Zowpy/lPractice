package net.lyragames.practice.match

import com.boydti.fawe.bukkit.chat.FancyMessage
import com.google.common.base.Joiner
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.constants.Constants
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.ArenaRatingManager
import net.lyragames.practice.manager.StatisticManager
import net.lyragames.practice.match.impl.MLGRushMatch
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.match.spectator.MatchSpectator
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.PlayerUtil
import net.lyragames.practice.utils.TextBuilder
import net.lyragames.practice.utils.TimeUtil
import net.lyragames.practice.utils.countdown.ICountdown
import net.lyragames.practice.utils.countdown.TitleCountdown
import net.lyragames.practice.utils.item.CustomItemStack
import net.lyragames.practice.utils.title.TitleBar
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

open class Match(val kit: Kit, val arena: Arena, val ranked: Boolean) {

    val uuid = UUID.randomUUID()
    var friendly = false
    var matchState = MatchState.STARTING
    var started = 0L
    val players: MutableList<MatchPlayer> = mutableListOf()
    val blocksPlaced: MutableList<Block> = mutableListOf()
    val droppedItems: MutableList<Item> = mutableListOf()
    val snapshots: MutableList<MatchSnapshot> = mutableListOf()
    val spectators: MutableList<MatchSpectator> = mutableListOf()
    val countdowns: MutableList<ICountdown> = mutableListOf()

    open fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            CustomItemStack.customItemStacks.removeIf { it.uuid == matchPlayer.uuid }

            if (kit.kitData.boxing || kit.kitData.combo) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
            }

            if (kit.kitData.combo) {
                player.noDamageTicks = 3
                player.maximumNoDamageTicks = 3
            }

            PlayerUtil.reset(player)
            PlayerUtil.denyMovement(player)

            player.teleport(matchPlayer.spawn)
            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            countdowns.add(TitleCountdown(
                player,
                "${CC.SECONDARY}<seconds>${CC.PRIMARY}...",
                "${CC.SECONDARY}<seconds>",
                null,
                6
            ) {
                player.sendMessage("${CC.PRIMARY}Match started!")
                matchState = MatchState.FIGHTING
                PlayerUtil.allowMovement(player)
                started = System.currentTimeMillis()
            })
        }
    }

    open fun getMatchType(): MatchType {
        if (this is TeamMatch) {
            return MatchType.TEAM
        }

        if (this is MLGRushMatch) {
            return MatchType.BEDFIGHTS
        }

        return MatchType.NORMAL
    }

    open fun addSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.SPECTATING
        profile?.spectatingMatch = uuid

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e started spectating!")
        }else {
            sendMessage("&7[S] &a${player.name}&e started spectating!", "lpractice.silent")
        }

        players.forEach {
            if (!it.offline) {
                player.showPlayer(it.player)
            }
        }

        PlayerUtil.reset(player)
        player.gameMode = GameMode.CREATIVE

        Hotbar.giveHotbar(profile)

        val matchSpectator = MatchSpectator(player.uniqueId, player.name)
        spectators.add(matchSpectator)
    }

    open fun removeSpectator(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e stopped spectating!")
        }else {
            sendMessage("&7[S] &a${player.name}&e stopped spectating!", "lpractice.silent")
        }

        removeSpec(player)
        spectators.removeIf { it.uuid == player.uniqueId }
    }

    private fun removeSpec(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.state = ProfileState.LOBBY
        profile?.spectatingMatch = null

        players.forEach {
            if (!it.offline) {
                player.hidePlayer(it.player)
            }
        }

        if (player != null) {
            PlayerUtil.reset(player)

            Hotbar.giveHotbar(profile!!)

            if (Constants.SPAWN != null) {
                player.teleport(Constants.SPAWN)
            }
        }
    }

    open fun forceRemoveSpectator(player: Player) {
        removeSpec(player)
        spectators.removeIf { it.uuid == player.uniqueId }
    }

    open fun canHit(player: Player, target: Player): Boolean {
        return true
    }

    open fun addPlayer(player: Player, location: Location) {
        val matchPlayer = MatchPlayer(player.uniqueId, player.name, location)
        players.add(matchPlayer)

        players.stream().map { it.player }
            .forEach {
                player.showPlayer(it)
                it.showPlayer(player)
            }
    }

    fun sendMessage(message: String) {
        players.stream().map { it.player }.forEach { it.sendMessage(CC.translate(message)) }
        spectators.stream().map { it.player }.forEach { it.sendMessage(CC.translate(message)) }
    }

    fun sendMessage(message: String, permission: String) {
        players.stream().map { it.player }.forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
        spectators.stream().map { it.player }.forEach { if (it.hasPermission(permission)) it.sendMessage(CC.translate(message)) }
    }

    open fun handleDeath(player: MatchPlayer) {
        player.dead = true

        if (player.offline) {

            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")

        } else if (player.lastDamager == null && !player.offline) {

            sendMessage("&c${player.name} ${CC.PRIMARY}has died from natural causes!")

        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        end(mutableListOf(player))
    }

    open fun end(losers: MutableList<MatchPlayer>) {
        reset()

        countdowns.forEach { it.cancel() }

        val winners = players.filter { !losers.contains(it) }
            .toMutableList()

        matchState = MatchState.ENDING

        winners.forEach { winner ->
            losers.forEach { winner.player.hidePlayer(it.player) }
        }

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val bukkitPlayer = matchPlayer.player
            val profile = Profile.getByUUID(matchPlayer.uuid)

            if (profile!!.arrowCooldown != null) {
                profile.arrowCooldown!!.cancel()
                profile.arrowCooldown = null
            }

            if (profile.enderPearlCooldown != null) {
                profile.enderPearlCooldown!!.cancel()
                profile.enderPearlCooldown = null
            }

            val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)

            snapshot.potionsThrown = matchPlayer.potionsThrown
            snapshot.potionsMissed = matchPlayer.potionsMissed
            snapshot.longestCombo = matchPlayer.longestCombo
            snapshot.totalHits = matchPlayer.hits
            snapshot.opponent = getOpponent(bukkitPlayer.uniqueId)?.uuid

            snapshots.add(snapshot)

            snapshot.createdAt = System.currentTimeMillis()
            MatchSnapshot.snapshots.add(snapshot)

            PlayerUtil.reset(bukkitPlayer)
            PlayerUtil.allowMovement(bukkitPlayer)
        }

        sendTitleBar(winners)
        endMessage(winners, losers)

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                val bukkitPlayer = matchPlayer.player
                val profile = Profile.getByUUID(matchPlayer.uuid)

                profile!!.match = null
                profile.state = ProfileState.LOBBY

                if (Constants.SPAWN != null) {
                    bukkitPlayer.teleport(Constants.SPAWN)
                }

                CustomItemStack.customItemStacks.removeIf { it.uuid == matchPlayer.uuid }

                Hotbar.giveHotbar(profile)

                players.stream().filter { !it.offline }.map { it.player }
                    .forEach {
                        if (it.player == null) return@forEach
                        bukkitPlayer.hidePlayer(it)
                        it.hidePlayer(bukkitPlayer)
                    }

                if (!losers.contains(matchPlayer) && !friendly) {
                    StatisticManager.win(profile, profile, kit, ranked)
                }else {
                    if (!friendly) {
                        StatisticManager.loss(profile, kit, ranked)
                    }
                }

                ratingMessage(profile)
            }

            if (spectators.isNotEmpty()) {
                for (i in 0 until spectators.size) {
                    val player = spectators[i].player ?: continue
                    removeSpec(player)
                }
            }

            spectators.clear()

            matches.remove(this)
            reset()
            arena.free = true
        }, 60L)
    }

    open fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        handleDeath(matchPlayer)
    }

    private fun ratingMessage(profile: Profile) {
        if (!profile.settings.mapRating) return
        if (ArenaRatingManager.hasRated(profile.uuid, arena)) return
        if (profile.player == null || !profile.player.isOnline) return

        val fancyMessage = FancyMessage()
            .text("${CC.PRIMARY}Rate The Map: ")
            .then()
            .text("${CC.DARK_RED}[1] ")
            .command("/ratemap ${arena.name} 1")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.RED}[2] ")
            .command("/ratemap ${arena.name} 2")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.YELLOW}[3] ")
            .command("/ratemap ${arena.name} 3")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.GREEN}[4] ")
            .command("/ratemap ${arena.name} 4")
            .tooltip("${CC.PRIMARY}Click to vote!")
            .then()
            .text("${CC.DARK_GREEN}[5] ")
            .command("/ratemap ${arena.name} 5")
            .tooltip("${CC.PRIMARY}Click to vote!")

        fancyMessage.send(profile.player)
    }

    open fun endMessage(winners: MutableList<MatchPlayer>, losers: MutableList<MatchPlayer>) {
        val fancyMessage = TextBuilder()
            .setText("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .setText("${CC.GREEN}Winner: ")
            .then()

        var wi = 1
        for (matchPlayer in winners) {
            if (wi < winners.size) {
                fancyMessage.setText("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.setText("${matchPlayer.name}\n")
            }

            fancyMessage.setCommand("/matchsnapshot ${matchPlayer.uuid}")
            fancyMessage.then()
            wi++
        }

        fancyMessage.setText("${CC.RED}Loser: ").then()

        var i = 1
        for (matchPlayer in losers) {

            if (i < losers.size) {
                fancyMessage.setText("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.setText("${matchPlayer.name}\n")
            }

            fancyMessage.setCommand("/matchsnapshot ${matchPlayer.uuid}")
            fancyMessage.then()
            i++
        }

        if (spectators.isNotEmpty()) {
            fancyMessage.setText("\n${CC.GREEN}Spectators ${CC.GRAY}(${spectators.size})${CC.GREEN}: ")
                .then().setText("${Joiner.on("${CC.GRAY}, ${CC.RESET}").join(spectators.map { it.name })}\n")
                .then().setText("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }else {
            fancyMessage.setText("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }

        val message = fancyMessage.build()

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            matchPlayer.player.spigot().sendMessage(message)
        }

        for (spectator in spectators) {
            val player = spectator.player ?: continue

            player.spigot().sendMessage(message)
        }
    }

    private fun sendTitleBar(winners: MutableList<MatchPlayer>) {
        val winnerString = Joiner.on(", ").join(winners.map { it.name }.toList())

        players.forEach {
            if (it.offline) return@forEach
            TitleBar.sendTitleBar(it.player, "${CC.SECONDARY}$winnerString${CC.PRIMARY} won!", null, 10, 40, 10) }
    }

    fun getTime(): String {
        if (matchState == MatchState.STARTING) {
            return "${CC.GREEN}Starting"
        }

        if (matchState == MatchState.ENDING) {
            return "${CC.RED}Ending"
        }

        return TimeUtil.millisToTimer(System.currentTimeMillis() - started)
    }

    open fun reset() {
        blocksPlaced.forEach { it.type = Material.AIR }
        droppedItems.forEach { it.remove() }
    }

    fun getMatchPlayer(uuid: UUID): MatchPlayer? {
        return players.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun getOpponent(uuid: UUID): MatchPlayer? {
        return players.stream().filter { it.uuid != uuid }
            .findFirst().orElse(null)
    }

    open fun getOpponentString(uuid: UUID): String? {
        return getOpponent(uuid)?.name
    }

    fun getAlivePlayers(): MutableList<MatchPlayer> {
        return players.stream().filter { !it.dead && !it.offline }
            .collect(Collectors.toList())
    }

    companion object {
        @JvmStatic
        val matches: MutableList<Match?> = mutableListOf()

        @JvmStatic
        fun getByUUID(uuid: UUID): Match? {
            return matches.stream().filter { match: Match? -> match?.uuid == uuid }
                .findFirst().orElse(null)
        }

        @JvmStatic
        fun inMatch(): Int {
            var count = 0

            for (match in matches) {
                count += match!!.players.size
            }

            return count
        }
    }
}