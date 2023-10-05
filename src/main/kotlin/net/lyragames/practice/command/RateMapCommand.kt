package net.lyragames.practice.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Name
import co.aikar.commands.annotation.Single
import co.aikar.commands.annotation.Subcommand
import net.lyragames.practice.Locale
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.ArenaRating
import net.lyragames.practice.manager.ArenaRatingManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

object RateMapCommand: BaseCommand() {

    @CommandAlias("ratemap")
    fun rate(player: CommandSender, @Single @Name("arena") arena: Arena, @Single @Name("star") int: Int) {
        val profile = Profile.getByUUID((player as Player).uniqueId)

        if (!profile?.settings?.mapRating!!) {
            player.sendMessage(Locale.DISABLED_MAP_RATING.getMessage())
            return
        }

        if (ArenaRatingManager.hasRated(player.uniqueId, arena)) {
            player.sendMessage(Locale.ALREADY_RATED.getMessage())
            return
        }

        val rating = ArenaRating(UUID.randomUUID(), int, player.uniqueId, arena.name)
        rating.save()

        ArenaRatingManager.arenaRatings.add(rating)

        player.sendMessage(Locale.THANK_YOU.getMessage())
    }
}