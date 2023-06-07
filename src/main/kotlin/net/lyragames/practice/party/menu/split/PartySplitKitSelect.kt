package net.lyragames.practice.party.menu.split

import net.lyragames.llib.utils.CC
import net.lyragames.menu.Button
import net.lyragames.menu.ItemBuilder
import net.lyragames.menu.Menu
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.party.Party
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/26/2022
 * Project: lPractice
 */

class PartySplitKitSelect(private val party: Party): Menu() {

    override fun getTitle(p0: Player?): String {
        return "Select a kit!"
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {

        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in Kit.kits) {
            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem).name("${CC.PRIMARY}${kit.name}").build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        if (party.players.size < 2) {
                            player.sendMessage("${CC.RED}You need at least 2 players to start a Split match!")
                            return
                        }

                        val arena = ArenaManager.getFreeArena(kit)

                        if (arena == null) {
                            player.sendMessage("${CC.RED}There is no free arenas!")
                            return
                        }

                        val match = TeamMatch(kit, arena, false)

                        for (uuid in party.players) {
                            val partyPlayer = Bukkit.getPlayer(uuid) ?: continue
                            val profile = Profile.getByUUID(uuid)

                            profile?.match = match.uuid
                            profile?.matchObject = match
                            profile?.state = ProfileState.MATCH
                            match.addPlayer(partyPlayer, arena.l1!!)
                        }

                        Match.matches.add(match)

                        player.closeInventory()
                        match.start()
                    }
                }
            }
        }

        return toReturn
    }
}