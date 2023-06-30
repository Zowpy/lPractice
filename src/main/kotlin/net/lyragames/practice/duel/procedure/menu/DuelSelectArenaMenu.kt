package net.lyragames.practice.duel.procedure.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.pagination.PaginatedMenu
import net.lyragames.practice.arena.Arena
import net.lyragames.practice.arena.type.ArenaType
import net.lyragames.practice.duel.procedure.DuelProcedure
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/28/2022
 * Project: lPractice
 */

class DuelSelectArenaMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Select an arena"
    }

    override fun onClose(player: Player?) {
        if (!isClosedByMenu) {

            DuelProcedure.duelProcedures.removeIf { it.uuid == player?.uniqueId }

        }
    }

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        val duelProcedure = DuelProcedure.getByUUID(player.uniqueId) ?: return toReturn

        for (arena in Arena.arenas) {
            if (!arena.isSetup || arena.duplicate) continue

            if (arena.arenaType == ArenaType.BUILD && !duelProcedure.kit!!.kitData.build) continue
            if (arena.arenaType == ArenaType.SUMO && !duelProcedure.kit!!.kitData.sumo) continue
            if (arena.arenaType == ArenaType.MLGRUSH && !duelProcedure.kit!!.kitData.mlgRush) continue
            if (arena.arenaType == ArenaType.BEDFIGHT && !duelProcedure.kit!!.kitData.bedFights) continue
            if (arena.arenaType == ArenaType.BRIDGE && !duelProcedure.kit!!.kitData.bridge) continue
            if (arena.arenaType == ArenaType.FIREBALL_FIGHT && !duelProcedure.kit!!.kitData.fireballFight) continue

            val kitData = duelProcedure.kit!!.kitData

            if (arena.arenaType == ArenaType.NORMAL && (kitData.build
                        || kitData.bedFights
                        || kitData.mlgRush
                        || kitData.sumo
                        || kitData.bridge
                        || kitData.fireballFight)) continue

            //if (arena.arenaType != ArenaType.NORMAL) continue

            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(Material.PAPER)
                        .name("${CC.PRIMARY}${arena.name}")
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        if (!arena.isFree()) {
                            player.sendMessage("${CC.RED}This arena is not free!")
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

        return toReturn
    }
}