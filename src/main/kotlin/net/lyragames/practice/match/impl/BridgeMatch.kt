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
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
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

        if (oppositeTeam!!.portal!!.contains(event.location)) {
            val titleBar = TitleBar("${CC.SECONDARY}${player.name}${CC.PRIMARY} scored!", false)
            players.forEach { if (!it.offline) titleBar.sendPacket(it.player) }

            team.points++

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

            reset()
            matchState = MatchState.STARTING

            for (matchPlayer in players) {
                if (matchPlayer.offline) continue

                if (matchPlayer.respawnCountdown != null) {
                    matchPlayer.respawnCountdown?.cancel()
                    matchPlayer.respawnCountdown?.consumer?.accept(true)
                    matchPlayer.respawnCountdown = null
                }

                val player = matchPlayer.player
                val profile = Profile.getByUUID(player.uniqueId)

                PlayerUtil.reset(player)
                PlayerUtil.denyMovement(player)

                player.teleport(matchPlayer.spawn)
                profile?.getKitStatistic(kit.name)?.generateBooks(player)

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

    override fun handleDeath(player: MatchPlayer) {
        if (player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}has disconnected!")
        } else if (player.lastDamager == null && !player.offline) {
            sendMessage("&c${player.name} ${CC.PRIMARY}fell in the void!")
        } else {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage("&c${player.name} ${CC.PRIMARY}has been killed by &c" + matchPlayer?.name + "${CC.PRIMARY}!")
        }

        player.respawning = true

        PlayerUtil.reset(player.player)

        player.player.gameMode = GameMode.SPECTATOR

        player.player.allowFlight = true
        player.player.isFlying = true

        players.stream().forEach { if (!it.offline) it.player.hidePlayer(player.player) }

        player.player.teleport(arena.bounds.center)

        val countdown = Countdown(
            PracticePlugin.instance,
            player.player,
            "${CC.PRIMARY}Respawning in ${CC.SECONDARY}<seconds>${CC.PRIMARY}!",
            6
        ) {
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
            val team = teams.stream().filter { team -> team.players.stream().noneMatch { !it.dead && !it.offline } }
                .findFirst().orElse(null)
            team!!.players.map { getMatchPlayer(it.uuid)!! }.toMutableList().let { end(it) }
        }
    }
}