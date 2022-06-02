package net.lyragames.practice.manager

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.rating.ArenaRating
import java.util.*

object ArenaRatingManager {

    val arenaRatings: MutableList<ArenaRating> = mutableListOf()

    fun load() {
        for (document in PracticePlugin.instance.practiceMongo.arenaRatings.find()) {
            val arenaRating = ArenaRating(UUID.fromString(document.getString("uuid")), document.getInteger("stars"), UUID.fromString(document.getString("user")), document.getString("arena"))

            arenaRatings.add(arenaRating)
        }
    }

    fun getArenaRatings(arena: Arena): MutableList<ArenaRating> {
        return arenaRatings.filter { it.arena.equals(arena.name, true) }.toCollection(mutableListOf())
    }

    fun hasRated(uuid: UUID, arena: Arena): Boolean {
        return !getArenaRatings(arena).none { it.user == uuid && it.arena.equals(arena.name, true) }
    }

    fun getAverageRating(arena: Arena): Double {
        val ratings = getArenaRatings(arena)

        val stars5 = ratings.filter { it.stars == 5 }.size
        val stars4 = ratings.filter { it.stars == 4 }.size
        val stars3 = ratings.filter { it.stars == 3 }.size
        val stars2 = ratings.filter { it.stars == 2 }.size
        val stars1 = ratings.filter { it.stars == 1 }.size

        val totalRatings = stars5 * 5 + stars4 * 4 + stars3 * 3 + stars2 * 2 + stars1

        return (totalRatings / 5).toDouble()
    }
}