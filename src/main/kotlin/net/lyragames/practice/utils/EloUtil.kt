package net.lyragames.practice.utils

import kotlin.math.pow

object EloUtil {

    private val K_FACTORS = arrayOf(
        KFactor(0, 1000, 25.0),
        KFactor(1001, 1400, 20.0),
        KFactor(1401, 1800, 15.0),
        KFactor(1801, 2200, 10.0)
    )

    private const val DEFAULT_K_FACTOR = 25
    private const val WIN = 1
    private const val LOSS = 0

    fun getNewRating(rating: Int, opponentRating: Int, won: Boolean): Int {
        return if (won) {
            getNewRating(rating, opponentRating, WIN)
        } else {
            getNewRating(rating, opponentRating, LOSS)
        }
    }

    private fun getNewRating(rating: Int, opponentRating: Int, score: Int): Int {
        val kFactor = getKFactor(rating)
        val expectedScore = getExpectedScore(rating, opponentRating)
        var newRating = calculateNewRating(rating, score, expectedScore, kFactor)
        if (score == 1) {
            if (newRating == rating) {
                newRating++
            }
        }
        return newRating
    }

    private fun calculateNewRating(oldRating: Int, score: Int, expectedScore: Double, kFactor: Double): Int {
        return oldRating + (kFactor * (score - expectedScore)).toInt()
    }

    private fun getKFactor(rating: Int): Double {
        for (i in K_FACTORS.indices) {
            if (rating >= K_FACTORS[i].startIndex && rating <= K_FACTORS[i].endIndex) {
                return K_FACTORS[i].value
            }
        }
        return DEFAULT_K_FACTOR.toDouble()
    }

    private fun getExpectedScore(rating: Int, opponentRating: Int): Double {
        return 1 / (1 + 10.0.pow((opponentRating - rating).toDouble() / 400))
    }

}