package net.lyragames.practice.party.duel.procedure.menu

import com.google.common.base.Joiner
import net.lyragames.menu.Button
import net.lyragames.menu.ItemBuilder
import net.lyragames.menu.pagination.PaginatedMenu
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.party.duel.procedure.PartyDuelProcedure
import net.lyragames.practice.profile.Profile
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
                        .name(Bukkit.getPlayer(party.leader).name)
                        .lore(listOf(
                            "&eMember Count: &5${party.players.size}",
                            "&eMembers: &5${Joiner.on("&7, &5").join(party.players.stream().map { Bukkit.getPlayer(it).name }.collect(
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