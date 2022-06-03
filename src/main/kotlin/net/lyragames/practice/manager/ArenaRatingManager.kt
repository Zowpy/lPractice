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
        val stars5 = getUsersRated(5, arena)
        val stars4 = getUsersRated(4, arena)
        val stars3 = getUsersRated(3, arena)
        val stars2 = getUsersRated(2, arena)
        val stars1 = getUsersRated(1, arena)

        val totalRatings = stars5 * 5 + stars4 * 4 + stars3 * 3 + stars2 * 2 + stars1

        return (totalRatings / arenaRatings.size).toDouble()
    }

    fun getUsersRated(int: Int, arena: Arena): Int {
        val ratings = getArenaRatings(arena)

        return ratings.filter { it.stars == int }.size
    }
}