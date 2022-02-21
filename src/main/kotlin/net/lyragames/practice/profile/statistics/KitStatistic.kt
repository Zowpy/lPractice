package net.lyragames.practice.profile.statistics

import net.lyragames.practice.kit.EditedKit


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

    var editedKits: MutableList<EditedKit?> = mutableListOf(null, null, null, null)

    fun replaceKit(index: Int, loadout: EditedKit?) {
        editedKits[index] = loadout
    }

    fun deleteKit(loadout: EditedKit?) {
        for (i in 0..3) {
            if (editedKits[i] == loadout) {
                editedKits[i] = null
                break
            }
        }
    }

    fun getKitCount(): Int {
        var i = 0
        for (editKit in editedKits) {
            if (editKit != null) {
                i++
            }
        }
        return i
    }

}