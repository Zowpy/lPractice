package net.lyragames.practice.utils

object PotionGradeUtil {

    fun getGrade(accuracy: Double): String {

        if (accuracy >= 100) {
            return "${CC.GOLD}S"
        }

        if (accuracy in 90.0..99.0) {
            return "${CC.GREEN}A"
        }

        if (accuracy in 80.0..89.0) {
            return "${CC.YELLOW}B"
        }

        if (accuracy in 70.0..79.0) {
            return "${CC.YELLOW}C"
        }

        if (accuracy in 60.0..69.0) {
            return "${CC.YELLOW}D"
        }

        if (accuracy in 0.0..59.0) {
            return "${CC.RED}F"
        }

        return "${CC.RED}N/A"
    }
}