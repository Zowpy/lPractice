package net.lyragames.practice.party.duel.procedure.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.duel.procedure.DuelProcedure
import net.lyragames.practice.duel.procedure.DuelProcedureStage
import net.lyragames.practice.duel.procedure.menu.DuelSelectArenaMenu
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.ArenaManager
import net.lyragames.practice.party.duel.procedure.PartyDuelProcedure
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/4/2022
 * Project: lPractice
 */

class PartyDuelKitSelectMenu: Menu() {

    override fun getTitle(p0: Player?): String {
        return "Select a kit"
    }

    override fun onClose(player: Player?) {
        if (!isClosedByMenu) {

            PartyDuelProcedure.duelProcedures.removeIf { it.issuer == player?.uniqueId }

        }
    }

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in Kit.kits) {
            toReturn[toReturn.size] = object : Button() {
                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem)
                        .name("${CC.YELLOW}${kit.name}")
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        val duelProcedure = PartyDuelProcedure.getByUUID(player.uniqueId)

                        if (duelProcedure == null) {
                            player.sendMessage("${CC.RED}Something went wrong!")
                            player.closeInventory()
                            return
                        }

                        duelProcedure.kit = kit

                        if (player.hasPermission("lpractice.duel.arena")) {
                            isClosedByMenu = true
                            PartyDuelArenaSelectMenu().openMenu(player)
                        }else {
                            val arena = ArenaManager.getFreeArena()

                            if (arena == null) {
                                player.sendMessage("${CC.RED}There is no free arena!")
                                isClosedByMenu = false
                                player.closeInventory()
                                return
                            }

                            duelProcedure.arena = arena
                            isClosedByMenu = true
                            player.closeInventory()

                            duelProcedure.create().send()

                            player.sendMessage("${CC.GREEN}Successfully sent duel request!")

                        }

                    }
                }
            }
        }

        return toReturn
    }
}