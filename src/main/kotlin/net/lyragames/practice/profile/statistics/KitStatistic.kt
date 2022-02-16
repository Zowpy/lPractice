package net.lyragames.practice.profile.statistics


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

class KitStatistic constructor(val kit: String) {

    var elo = 1000
    var peakELO = 1000
    var wins = 0

    var rankedWins = 0
    var currentStreak = 0
    var bestStreak = 0
}