package net.lyragames.practice.party.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.pagination.PaginatedMenu
import net.lyragames.practice.party.Party
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/24/2022
 * Project: lPractice
 */

class PartyPlayersMenu(private val party: Party): PaginatedMenu() {

    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Party Players"
    }

    override fun getSize(): Int {
        return 36
    }

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        party.players.stream().map { Bukkit.getPlayer(it) }
            .forEach {
                toReturn[toReturn.size] = object: Button() {

                    override fun getButtonItem(p0: Player?): ItemStack {
                        return ItemBuilder(Material.IRON_SWORD)
                            .name("&e${it.name}")
                            .lore(listOf(
                                "&eUUID: &a${it.uniqueId.toString()}",
                                "&eName: &a${it.name}",
                                "",
                                "&e&o(( left click to kick player ))",
                                "&e&o(( right click to ban player ))"
                            ))
                            .skullBuilder()
                            .setOwner(it.name)
                            .buildSkull()
                    }

                    override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {


                        if (player?.uniqueId != party.leader) {
                            player?.sendMessage("${CC.RED}You can't do this!")
                            return
                        }

                        val profile = Profile.getByUUID(it.uniqueId)

                        if (clickType?.isRightClick!!) {
                            party.banned.add(it.uniqueId)
                            party.players.remove(it.uniqueId)

                            profile?.party = null
                            party.sendMessage("&e${it.name}&a has been banned from the party!")
                        }else if (clickType.isLeftClick) {
                            party.players.remove(it.uniqueId)

                            profile?.party = null
                            party.sendMessage("&e${it.name}&a has been kicked from the party!")
                        }

                        player.closeInventory()
                        Hotbar.giveHotbar(profile!!)
                    }

                }
            }

        return toReturn
    }
}