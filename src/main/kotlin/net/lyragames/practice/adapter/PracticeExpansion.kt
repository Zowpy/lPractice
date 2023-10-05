package net.lyragames.practice.adapter

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.lyragames.practice.match.Match
import net.lyragames.practice.profile.Profile
import org.bukkit.entity.Player

class PracticeExpansion: PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "practice"
    }

    override fun getAuthor(): String {
        return "hyptex"
    }

    override fun getVersion(): String {
        return "1.0"
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        val profile = Profile.getByUUID(player!!.uniqueId)

        if (params.contains("elo", ignoreCase = true)) {
            val splitString: List<String> = params.split("_")
            val kitName = splitString[0]
            val elo: Int = profile!!.getKitStatistic(kitName)!!.elo
            return elo.toString()
        }
        return ""
    }

}