package net.lyragames.practice.command

import me.zowpy.command.annotation.Command
import me.zowpy.command.annotation.Named
import me.zowpy.command.annotation.Sender
import net.lyragames.practice.Locale
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.ArenaRating
import net.lyragames.practice.manager.ArenaRatingManager
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player
import java.util.*

object RateMapCommand {

    @Command(name = "ratemap")
    fun rate(@Sender player: Player, @Named("arena") arena: Arena, @Named("stars") int: Int) {
        val profile = Profile.getByUUID(player.uniqueId)

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