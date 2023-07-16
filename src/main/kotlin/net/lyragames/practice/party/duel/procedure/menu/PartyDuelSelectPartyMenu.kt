package net.lyragames.practice.party.duel.procedure.menu

import com.google.common.base.Joiner
import me.zowpy.menu.buttons.Button
import me.zowpy.menu.pagination.PaginatedMenu
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.party.duel.procedure.PartyDuelProcedure
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/5/2022
 * Project: lPractice
 */

class PartyDuelSelectPartyMenu: PaginatedMenu() {

    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Select a party!"
    }

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        val profile = Profile.getByUUID(player.uniqueId)

        for (party in PartyManager.parties) {
            if (profile?.party != null && profile.party == party.uuid) continue

            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(Material.NETHER_STAR)
                        .name("${CC.PRIMARY}${Bukkit.getPlayer(party.leader).name}")
                        .lore(listOf(
                            "${CC.PRIMARY}Member Count: ${CC.SECONDARY}${party.players.size}",
                            "${CC.PRIMARY}Members: ${CC.SECONDARY}${Joiner.on("&7, ${CC.SECONDARY}").join(party.players.stream().map { Bukkit.getPlayer(it).name }.collect(
                                Collectors.toList()))}"
                        )).build()
                }

                override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        val duelProcedure = PartyDuelProcedure.getByUUID(player?.uniqueId!!)

                        duelProcedure?.party = party.uuid

                        isClosedByMenu = true
                        PartyDuelKitSelectMenu().openMenu(player)
                    }
                }

            }
        }

        return toReturn
    }
}