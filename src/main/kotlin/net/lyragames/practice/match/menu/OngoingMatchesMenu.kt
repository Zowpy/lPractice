package net.lyragames.practice.match.menu

import me.zowpy.menu.Menu
import me.zowpy.menu.buttons.Button
import net.lyragames.practice.match.Match
import net.lyragames.practice.utils.CC
import org.bukkit.entity.Player

class OngoingMatchesMenu: Menu() {
    override fun getTitle(p0: Player?) = "${CC.PRIMARY}Ongoing Matches"

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        val currentMatches = Match.inMatch()



        return buttons
    }
}