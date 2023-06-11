package net.lyragames.practice.match.impl

import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.title.TitleBar
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.bedwars.StandaloneBedWarsArena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.team.Team
import net.lyragames.practice.profile.Profile
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/31/2022
 * Project: lPractice
 */

class BedFightMatch(kit: Kit, arena: Arena, ranked: Boolean) : TeamMatch(kit, arena, ranked) {

    init {
        teams.clear()

        val team1 = Team("Red")
        team1.spawn = (arena as StandaloneBedWarsArena).redSpawn
        team1.bedLocation = arena.redBed

        val team2 = Team("Blue")
        team2.spawn = arena.blueSpawn
        team2.bedLocation = arena.blueBed

        teams.add(team1)
        teams.add(team2)
    }

    override fun addPlayer(player: Player, location: Location) {
        val team = findTeam()
        val teamMatchPlayer = TeamMatchPlayer(player.uniqueId, player.name, team?.spawn!!, team.uuid!!)

        team.players.add(teamMatchPlayer)

        players.stream().map { it.player }.forEach {
            it.showPlayer(player)
            player.showPlayer(it)
        }
        players.add(teamMatchPlayer)
    }

    override fun start() {
        for (matchPlayer in players) {
            if (matchPlayer.offline) continue

            val player = matchPlayer.player
            PlayerUtil.denyMovement(player)

            val profile = Profile.getByUUID(player.uniqueId)

            PlayerUtil.reset(player)

            val team = getTeam((matchPlayer as TeamMatchPlayer).teamUniqueId)

            matchPlayer.spawn = team?.spawn!!
            matchPlayer.bed = team.bedLocation

            player.teleport(matchPlayer.spawn)

            CustomItemStack.getCustomItemStacks().removeIf { it.uuid == matchPlayer.uuid }

            profile?.getKitStatistic(kit.name)?.generateBooks(player)

            countdowns.add(Countdown(
                PracticePlugin.instance,
                player,
                "&aMatch starting in <seconds> seconds!",
                6
            ) {
                player.sendMessage(CC.GREEN + "Match started!")
                matchState = MatchState.FIGHTING
                started = System.currentTimeMillis()
                PlayerUtil.allowMovement(player)
            })
        }
    }

    fun handleBreak(event: BlockBreakEvent) {

        val player = getMatchPlayer(event.player.uniqueId)

        if (player!!.dead || player.respawning) {
            event.isCancelled = true
            return
        }

        if (blocksPlaced.contains(event.block)) {
            blocksPlaced.remove(event.block)
        } else {
            event.isCancelled = true
        }

        if (event.block.type == Material.BED || event.block.type == Material.BED_BLOCK) {
            for (matchPlayer in players) {
                for (x in event.block.x - 2 until event.block.x + 2) {
                    for (y in event.block.y - 2 until event.block.y + 2) {
                        for (z in event.block.z - 2 until event.block.z + 2) {
                            if (matchPlayer.bed?.blockX == x && matchPlayer.bed?.blockY == y && matchPlayer.bed?.blockZ == z) {

                                if (player.bed?.blockX == x && player.bed?.blockY == y && player.bed?.blockZ == z) {
                                    player.player.sendMessage("${CC.RED}You cannot break your own bed.")
                                    event.isCancelled = true
                                    break
                                }

                                val team = getTeam((matchPlayer as TeamMatchPlayer).teamUniqueId)

                                if (team!!.broken) {
                                    player.player.sendMessage("${CC.RED}The bed has already been broken.")
                                    break
                                }

                                val titleBar = TitleBar("${CC.RED}BED DESTROYED!", false)

                                team.players.forEach { if (!it.offline) titleBar.sendPacket(it.player) }

                                sendMessage("${CC.SECONDARY}${player.name}${CC.PRIMARY} broke ${CC.SECONDARY}${team.name}${CC.PRIMARY}'s bed!")

                                team.broken = true
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleDeath(player: MatchPlayer) {

        val team = getTeam((player as TeamMatchPlayer).teamUniqueId)

        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}fell in the void!${if (team?.broken!!) "${CC.GRAY} (${CC.SECONDARY}FINAL${CC.GRAY})" else ""}")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!${if (team?.broken!!) "${CC.GRAY} (${CC.SECONDARY}FINAL${CC.GRAY})" else ""}")
        }

        /*if (player.offline && team?.players?.none { !it.dead && !it.offline }!!) {
            end(team.players.map { getMatchPlayer(it.uuid)!! }.toMutableList())
            return
        }*/

        player.respawning = true

        if (team?.broken!!) {
            PlayerUtil.reset(player.player)

            player.dead = true

            player.player.allowFlight = true
            player.player.isFlying = true

            players.stream().forEach { if (!it.offline) it.player.hidePlayer(player.player) }

            player.player.teleport(arena.bounds.center)

            if (!team.players.any { !it.dead && !it.offline }) {
                end(team.players.map { getMatchPlayer(it.uuid)!! }.toMutableList())
            }

            return
        }

        PlayerUtil.reset(player.player)

        player.player.allowFlight = true
        player.player.isFlying = true

        players.stream().forEach { if (!it.offline) it.player.hidePlayer(player.player) }

        player.player.teleport(arena.bounds.center)

        val countdown = Countdown(PracticePlugin.instance, player.player, "${CC.PRIMARY}Respawning in ${CC.SECONDARY}<seconds>${CC.PRIMARY}!", 6) {
            val profile = Profile.getByUUID(player.uuid)
            player.player.teleport(player.spawn)

            PlayerUtil.reset(player.player)

            profile?.getKitStatistic(kit.name)?.generateBooks(player.player)

            player.respawning = false

            players.stream().forEach { if (!it.offline) it.player.showPlayer(player.player) }

            player.respawnCountdown = null
        }

        countdowns.add(countdown)
        player.respawnCountdown = countdown
    }

    override fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        if (teams.stream().anyMatch { team -> team.players.stream().noneMatch { !it.offline } }) {
            val team = teams.stream().filter { team -> team.players.stream().noneMatch { !it.dead && !it.offline } }.findFirst().orElse(null)
            team!!.players.map { getMatchPlayer(it.uuid)!! }.toMutableList().let { end(it) }
        }
    }
}