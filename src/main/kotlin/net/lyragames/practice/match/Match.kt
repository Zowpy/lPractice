package net.lyragames.practice.match

import com.google.common.base.Joiner
import mkremins.fanciful.FancyMessage
import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.title.TitleBar
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.llib.utils.TimeUtil
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
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Item
import org.bukkit.entity.Player
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
    val countdowns: MutableList<Countdown> = mutableListOf()

    open fun start() {

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            val profile = Profile.getByUUID(player.uniqueId)

            CustomItemStack.getCustomItemStacks().removeIf { it.uuid == matchPlayer.uuid }

            PlayerUtil.reset(player)
            PlayerUtil.denyMovement(player)

            player.teleport(matchPlayer.spawn)
            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            countdowns.add(Countdown(
                PracticePlugin.instance,
                player,
                "&aMatch starting in <seconds> seconds!",
                6
            ) {
                PlayerUtil.allowMovement(player)
                player.sendMessage(CC.GREEN + "Match started!")
                matchState = MatchState.FIGHTING
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
        profile?.state = ProfileState.LOBBY
        profile?.spectatingMatch = null

        if (!profile?.silent!!) {
            sendMessage("&a${player.name}&e stopped spectating!")
        }else {
            sendMessage("&7[S] &a${player.name}&e stopped spectating!", "lpractice.silent")
        }

        players.forEach {
            if (!it.offline) {
                player.hidePlayer(it.player)
            }
        }

        if (player != null) {
            PlayerUtil.reset(player)

            Hotbar.giveHotbar(profile)

            if (Constants.SPAWN != null) {
                player.teleport(Constants.SPAWN)
            }
        }

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

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                val bukkitPlayer = matchPlayer.player
                val profile = Profile.getByUUID(matchPlayer.uuid)

                val snapshot = MatchSnapshot(bukkitPlayer, matchPlayer.dead)
                snapshot.potionsThrown = matchPlayer.potionsThrown
                snapshot.potionsMissed = matchPlayer.potionsMissed
                snapshot.longestCombo = matchPlayer.longestCombo
                snapshot.totalHits = matchPlayer.hits
                snapshot.opponent = getOpponent(bukkitPlayer.uniqueId)?.uuid

                snapshots.add(snapshot)

                PlayerUtil.reset(bukkitPlayer)
                PlayerUtil.allowMovement(bukkitPlayer)
                profile?.match = null
                profile?.state = ProfileState.LOBBY

                if (Constants.SPAWN != null) {
                    bukkitPlayer.teleport(Constants.SPAWN)
                }

                CustomItemStack.getCustomItemStacks().removeIf { it.uuid == matchPlayer.uuid }

                Hotbar.giveHotbar(profile!!)

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

            for (snapshot in snapshots) {
                snapshot.createdAt = System.currentTimeMillis()
                MatchSnapshot.snapshots.add(snapshot)
            }

            sendTitleBar(winners)
            endMessage(winners, losers)

            if (spectators.isNotEmpty()) {
                for (i in 0 until spectators.size) {
                    val spectator = spectators[i]

                    if (spectator.player == null) continue

                    removeSpec(spectator.player)
                }
            }

            spectators.clear()

            matches.remove(this)
            reset()
            arena.free = true
        }, 20L)
    }

    open fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        handleDeath(matchPlayer)
    }

    fun ratingMessage(profile: Profile) {
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
        val fancyMessage = FancyMessage()
            .text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------\n")
            .then()
            .text("${CC.GREEN}Winner: ")
            .then()

        var wi = 1
        for (matchPlayer in winners) {
            if (wi < winners.size) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}\n")
            }

            fancyMessage.command("/matchsnapshot ${matchPlayer.uuid.toString()}")
            fancyMessage.then()
            wi++
        }

        fancyMessage.text("${CC.RED}Loser: ").then()

        var i = 1
        for (matchPlayer in losers) {

            if (i < losers.size) {
                fancyMessage.text("${matchPlayer.name}${CC.GRAY}, ")
            }else {
                fancyMessage.text("${matchPlayer.name}\n")
            }

            fancyMessage.command("/matchsnapshot ${matchPlayer.uuid.toString()}")
            fancyMessage.then()
            i++
        }

        if (spectators.isNotEmpty()) {
            fancyMessage.text("\n${CC.GREEN}Spectators ${CC.GRAY}(${spectators.size})${CC.GREEN}: ")
                .then().text("${Joiner.on("${CC.GRAY}, ${CC.RESET}").join(spectators.map { it.name })}\n")
                .then().text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }else {
            fancyMessage.text("${CC.GRAY}${CC.STRIKE_THROUGH}---------------------------")
        }

        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            fancyMessage.send(matchPlayer.player)
        }

        for (spectator in spectators) {
            if (spectator.player == null) continue

            fancyMessage.send(spectator.player)
        }
    }

    fun sendTitleBar(winners: MutableList<MatchPlayer>) {
        val winnerString = Joiner.on(", ").join(winners.map { it.name }.toList())

        val titleBar = TitleBar("${CC.GREEN}$winnerString${CC.YELLOW} won!", false)

        players.forEach { if (!it.offline) titleBar.sendPacket(it.player) }
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

    fun reset() {
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