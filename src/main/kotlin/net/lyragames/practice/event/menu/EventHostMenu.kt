package net.lyragames.practice.event.menu

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.event.impl.SumoEvent
import net.lyragames.practice.event.impl.TNTRunEvent
import net.lyragames.practice.event.impl.TNTTagEvent
import net.lyragames.practice.event.map.type.EventMapType
import net.lyragames.practice.event.menu.brackets.EventSelectKitMenu
import net.lyragames.practice.event.procedure.BracketEventProcedure
import net.lyragames.practice.manager.EventManager
import net.lyragames.practice.manager.EventMapManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 3/21/2022
 * Project: lPractice
 */

class EventHostMenu: Menu() {

    override fun getTitle(player: Player): String {
        return "Host events"
    }

    override fun getSize(): Int {
        return 27
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        toReturn[10] = object : Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.LEASH).name("${CC.PRIMARY}Sumo").build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (!player.hasPermission("lpractice.event.host")) {
                    player.sendMessage("${CC.RED}You don't have permissions to host this event!")
                    return
                }

                if (EventManager.event != null) {
                    player.sendMessage("${CC.RED}There is already a running event!")
                    return
                }

                val map = EventMapManager.getFreeMap(EventMapType.SUMO)

                if (map == null) {
                    player.sendMessage("${CC.RED}There are no event maps available!")
                    return
                }

                val event = SumoEvent(player.uniqueId, map)
                EventManager.event = event

                event.addPlayer(player)
            }

        }

        toReturn[12] = object : Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.TNT).name("${CC.PRIMARY}TNT Tag").build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (!player.hasPermission("lpractice.event.host")) {
                    player.sendMessage("${CC.RED}You don't have permissions to host this event!")
                    return
                }

                if (EventManager.event != null) {
                    player.sendMessage("${CC.RED}There is already a running event!")
                    return
                }

                val map = EventMapManager.getFreeMap(EventMapType.TNT_TAG)

                if (map == null) {
                    player.sendMessage("${CC.RED}There are no event maps available!")
                    return
                }

                val event = TNTTagEvent(player.uniqueId, map)
                EventManager.event = event

                event.addPlayer(player)
            }
        }

        toReturn[14] = object : Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.TNT).name("${CC.PRIMARY}TNT Run").build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (!player.hasPermission("lpractice.event.host")) {
                    player.sendMessage("${CC.RED}You don't have permissions to host this event!")
                    return
                }

                if (EventManager.event != null) {
                    player.sendMessage("${CC.RED}There is already a running event!")
                    return
                }

                val map = EventMapManager.getFreeMap(EventMapType.TNT_RUN)

                if (map == null) {
                    player.sendMessage("${CC.RED}There are no event maps available!")
                    return
                }

                val event = TNTRunEvent(player.uniqueId, map)
                EventManager.event = event

                event.addPlayer(player)
            }
        }

        toReturn[16] = object : Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.DIAMOND_SWORD).name("${CC.PRIMARY}Brackets").build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (!player.hasPermission("lpractice.event.host")) {
                    player.sendMessage("${CC.RED}You don't have permissions to host this event!")
                    return
                }

                if (EventManager.event != null) {
                    player.sendMessage("${CC.RED}There is already a running event!")
                    return
                }

                val map = EventMapManager.getFreeMap(EventMapType.BRACKETS)

                if (map == null) {
                    player.sendMessage("${CC.RED}There are no event maps available!")
                    return
                }

                val procedure = BracketEventProcedure(player.uniqueId, map)
                BracketEventProcedure.procedures[player.uniqueId] = procedure

                isClosedByMenu = true
                EventSelectKitMenu().openMenu(player)
            }

        }

        return toReturn
    }
}