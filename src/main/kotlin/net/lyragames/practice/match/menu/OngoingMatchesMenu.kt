package net.lyragames.practice.match.menu

import net.lyragames.llib.utils.CC
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.match.Match
import org.bukkit.entity.Player

class OngoingMatchesMenu: Menu() {
    override fun getTitle(p0: Player?) = "${CC.PRIMARY}Ongoing Matches"

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        val currentMatches = Match.inMatch()



        return buttons
    }
}