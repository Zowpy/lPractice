package net.lyragames.practice.match.impl

import net.lyragames.llib.title.TitleBar
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.Countdown
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.impl.bridge.StandaloneBridgeArena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.MatchState
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.player.TeamMatchPlayer
import net.lyragames.practice.match.team.Team
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.countdown.TitleCountdown
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPortalEnterEvent

class BridgeMatch(kit: Kit, arena: Arena, ranked: Boolean) : TeamMatch(kit, arena, ranked) {

    private var round = 1
    private var threshold: Long = 0L

    init {
        teams.clear()

        val team1 = Team("Red")
        team1.spawn = (arena as StandaloneBridgeArena).redSpawn
        team1.portal = arena.redPortal

        for (block in arena.redPortal!!.blocks) {
            block.type = Material.ENDER_PORTAL
        }

        val team2 = Team("Blue")
        team2.spawn = arena.blueSpawn
        team2.portal = arena.bluePortal

        for (block in arena.bluePortal!!.blocks) {
            block.type = Material.ENDER_PORTAL
        }

        teams.add(team1)
        teams.add(team2)
    }

    override fun addPlayer(player: Player, location: Location) {
        val team = findTeam()
        val teamMatchPlayer = TeamMatchPlayer(player.uniqueId, player.name, team?.spawn!!, team.uuid)

        val blue = team.name == "Blue"

        teamMatchPlayer.coloredName = if (blue) "${CC.BLUE}${player.name}" else "${CC.RED}${player.name}"

        team.players.add(teamMatchPlayer)

        players.stream().map { it.player }.forEach {
            it.showPlayer(player)
            player.showPlayer(it)
        }
        players.add(teamMatchPlayer)
    }

    fun handlePortal(event: EntityPortalEnterEvent) {

        // || event.from.block != null && event.from.block.type == Material.ENDER_PORTAL)

        if (System.currentTimeMillis() - threshold <= 100) {
            return
        }

        if (matchState != MatchState.FIGHTING) return

        val player = event.entity as Player

        val matchPlayer = getMatchPlayer(player.uniqueId) as TeamMatchPlayer

        if (matchPlayer.dead || matchPlayer.respawning) return

        val team = getTeam(matchPlayer.teamUniqueId)
        val oppositeTeam = getOpponentTeam(team!!)

        val blue = team.name == "Blue"

        if (oppositeTeam!!.portal!!.contains(event.location)) {
            team.points++

            val titleBar = TitleBar("${matchPlayer.coloredName}${CC.PRIMARY} scored!", false)
            val subTitle = TitleBar(
                "${CC.BLUE}${if (blue) team.points else oppositeTeam.points} ${CC.GRAY}- ${CC.RED}${if (blue) oppositeTeam.points else team.points}",
                true
            )

            players.forEach {
                if (!it.offline) {
                    titleBar.sendPacket(it.player)
                    subTitle.sendPacket(it.player)
                }
            }

            threshold = System.currentTimeMillis()

            resetMatch(team)
        }
    }

    private fun resetMatch(score: Team) {
        if (score.points >= 5) {
            end(getOpponentTeam(score)!!.players.map { it }.toMutableList())
        } else {
            round++

            countdowns.forEach { it.cancel() }

            matchState = MatchState.STARTING

            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                val player = matchPlayer.player
                val profile = Profile.getByUUID(player.uniqueId)

                if (profile!!.arrowCooldown != null) {
                    profile.arrowCooldown!!.cancel()
                    profile.arrowCooldown = null
                }

                PlayerUtil.reset(player)
                PlayerUtil.denyMovement(player)

                player.teleport(matchPlayer.spawn)
                profile.getKitStatistic(kit.name)?.generateBooks(player)

                countdowns.add(Countdown(
                    PracticePlugin.instance,
                    player,
                    "${CC.PRIMARY}Round ${CC.SECONDARY}$round ${CC.PRIMARY}starting in ${CC.SECONDARY}<seconds>${CC.PRIMARY} seconds!",
                    6
                ) {
                    player.sendMessage("${CC.PRIMARY}Round started!")
                    matchState = MatchState.FIGHTING
                    PlayerUtil.allowMovement(player)
                })
            }
        }
    }

    fun handlePlace(event: BlockPlaceEvent) {
        var found = false

        for (team in teams) {
            if (team.portal!!.contains(event.block.x, team.portal!!.y1, event.block.z)) {
                event.isCancelled = true
                found = true
                break
            }
        }

        if (!found) {
            blocksPlaced.add(event.block)
        }
    }

    override fun handleDeath(matchPlayer: MatchPlayer) {
        if (matchPlayer.offline) {
            sendMessage("&c${matchPlayer.coloredName} ${CC.PRIMARY}has disconnected!")
        } else if (matchPlayer.lastDamager == null && !matchPlayer.offline) {
            sendMessage("&c${matchPlayer.coloredName} ${CC.PRIMARY}was killed!")
        } else {
            val killer = getMatchPlayer(matchPlayer.lastDamager!!)

            sendMessage("${matchPlayer.coloredName} ${CC.PRIMARY}has been killed by " + killer?.coloredName + "${CC.PRIMARY}!")
        }

        val profile = Profile.getByUUID(matchPlayer.uuid)

        matchPlayer.respawning = true

        Bukkit.getScheduler().runTaskLater(PracticePlugin.instance, {
            matchPlayer.player.teleport(matchPlayer.spawn)
            PlayerUtil.reset(matchPlayer.player)

            profile?.getKitStatistic(kit.name)?.generateBooks(matchPlayer.player)

            matchPlayer.respawning = false
        }, 2L)
    }


    override fun handleQuit(matchPlayer: MatchPlayer) {
        matchPlayer.offline = true

        if (teams.any { team -> team.players.none { !it.offline } }) {
            val team = teams.firstOrNull { team -> team.players.none { !it.dead && !it.offline } }

            team!!.players.map { getMatchPlayer(it.uuid)!! }.toMutableList().let { end(it) }
        }
    }
}