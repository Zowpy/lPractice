package net.lyragames.practice.manager

import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.EloUtil

/**
 * This Project is property of Zowpy © 2023
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 6/7/2023
 * Project: lPractice
 */

object StatisticManager
{

    fun win(profile: Profile, loserProfile: Profile, kit: Kit, ranked: Boolean)
    {
        val globalStatistics = profile.globalStatistic

        globalStatistics.wins++
        globalStatistics.streak++

        if (globalStatistics.streak >= globalStatistics.bestStreak)
        {
            globalStatistics.bestStreak = globalStatistics.streak
        }

        val kitStatistic = profile.getKitStatistic(kit.name)!!

        kitStatistic.wins++

        // ELO
        if (ranked)
        {
            kitStatistic.rankedWins++

            val loserKitStatistic = loserProfile.getKitStatistic(kit.name)

            val loserElo = loserKitStatistic!!.elo
            val winnerElo = kitStatistic.elo

            val newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false)
            val newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true)

            loserKitStatistic.elo = newLoserElo
            kitStatistic.elo = newWinnerElo

            loserProfile.save()

            if (kitStatistic.elo >= kitStatistic.peakELO)
            {
                kitStatistic.peakELO = kitStatistic.elo
            }
        }

        kitStatistic.currentStreak++

        if (kitStatistic.currentStreak >= kitStatistic.bestStreak)
        {
            kitStatistic.bestStreak = kitStatistic.currentStreak
        }

        profile.save()
    }

    fun loss(profile: Profile, kit: Kit, ranked: Boolean)
    {
        val globalStatistics = profile.globalStatistic

        globalStatistics.losses++
        globalStatistics.streak = 0

        val kitStatistic = profile.getKitStatistic(kit.name)!!

        kitStatistic.losses++
        kitStatistic.currentStreak = 0

        if (ranked)
        {
            kitStatistic.rankedLosses++
        }

        profile.save()
    }
}