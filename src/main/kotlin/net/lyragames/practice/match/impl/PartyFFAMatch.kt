package net.lyragames.practice.match.impl

import net.lyragames.practice.Locale
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.player.MatchPlayer
import net.lyragames.practice.match.snapshot.MatchSnapshot
import org.bukkit.Material

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class PartyFFAMatch(kit: Kit, arena: Arena) : Match(kit, arena, false, true)
{

    override fun handleDeath(player: MatchPlayer)
    {
        player.dead = true

        if (player.lastDamager == null)
        {
            sendMessage(Locale.PLAYER_DIED.getMessage().replace("<player>", player.coloredName))
        } else
        {
            val matchPlayer = getMatchPlayer(player.lastDamager!!)

            sendMessage(
                Locale.PLAYED_KILLED.getMessage().replace("<player>", player.coloredName)
                    .replace("<killer>", matchPlayer!!.coloredName)
            )
        }

        val winner = getAlivePlayers()[0]

        if (getAlivePlayers().size <= 1)
        {
            end(players.filter { it.uuid != winner.uuid }.toMutableList())
        } else
        {
            val bukkitPlayer = player.player

            val snapshot = MatchSnapshot(bukkitPlayer, player.dead)
            snapshot.potionsThrown = player.potionsThrown
            snapshot.potionsMissed = player.potionsMissed
            snapshot.longestCombo = player.longestCombo

            snapshots.add(snapshot)

            val location = bukkitPlayer.location
            bukkitPlayer.teleport(location.add(0.0, 4.0, 0.0))

            addSpectator(bukkitPlayer)

            snapshot.contents.forEach {
                if (it == null || it.type == Material.AIR) return@forEach

                droppedItems.add(location.world.dropItemNaturally(location, it))
            }

            snapshot.armor.forEach {
                if (it == null || it.type == Material.AIR) return@forEach

                droppedItems.add(location.world.dropItemNaturally(location, it))
            }

            player.onlineProfile?.enderPearlCooldown = null

            players.stream().map { it.player }
                .forEach {
                    it.hidePlayer(bukkitPlayer)
                }
        }
    }
}