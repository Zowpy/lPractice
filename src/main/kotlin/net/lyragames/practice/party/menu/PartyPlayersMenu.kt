package net.lyragames.practice.party.menu

import me.zowpy.menu.buttons.Button
import me.zowpy.menu.pagination.PaginatedMenu
import net.lyragames.practice.party.Party
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.hotbar.Hotbar
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.ItemBuilder
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
                            if (player?.uniqueId == party.leader) return;
                            party.banned.add(it.uniqueId)
                            party.players.remove(it.uniqueId)

                            profile?.party = null
                            Hotbar.giveHotbar(profile!!)
                            party.sendMessage("${CC.SECONDARY}${it.name}${CC.PRIMARY} has been banned from the party!")
                        }else if (clickType.isLeftClick) {
                            if (player?.uniqueId == party.leader) return;
                            party.players.remove(it.uniqueId)

                            profile?.party = null
                            Hotbar.giveHotbar(profile!!)
                            party.sendMessage("${CC.SECONDARY}${it.name}${CC.PRIMARY} has been kicked from the party!")
                        }

                        player.closeInventory()
                        Hotbar.giveHotbar(profile!!)
                    }

                }
            }

        return toReturn
    }
}