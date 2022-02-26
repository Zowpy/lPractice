package net.lyragames.practice.match.ffa.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.match.ffa.FFAPlayer
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class FFAChoosingMenu: Menu() {

    override fun getTitle(p0: Player?): String {
        return "FFA"
    }

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {

        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in Kit.kits) {
            if (!kit.kitData.ffa || kit.kitData.build) continue

            val ffa = FFAManager.getByKit(kit)

            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem).name("${CC.YELLOW}${kit.name}")
                        .lore("&eCurrently playing: ${CC.PINK}${ffa?.players?.size}")
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {
                        val profile = Profile.getByUUID(player.uniqueId)

                        profile?.state = ProfileState.FFA
                        profile?.ffa = ffa?.uuid

                        val ffaPlayer = FFAPlayer(player.uniqueId, player.name)
                        ffa?.players?.add(ffaPlayer)

                        ffa?.setup(ffaPlayer)

                        player.closeInventory()
                        player.sendMessage("${CC.GREEN}Successfully joined FFA!")
                    }
                }
            }
        }

        return toReturn
    }
}