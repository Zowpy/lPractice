package net.lyragames.practice.kit.admin

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.queue.Queue
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

class AdminKitEditMenu(private val kit: Kit): Menu() {

    override fun getTitle(player: Player): String {
        return "&6Editing ${kit.name}"
    }

    override fun isUpdateAfterClick(): Boolean {
        return true
    }

    override fun getSize(): Int {
        return 27
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        toReturn[3] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.COBBLESTONE)
                    .name(CC.YELLOW + "Build")
                    .lore(listOf(
                        if (kit.kitData.build) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.build) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.build = !kit.kitData.build
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }


        toReturn[5] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.FENCE)
                    .name(CC.YELLOW + "HCF")
                    .lore(listOf(
                        if (kit.kitData.hcf) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.hcf) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.hcf = !kit.kitData.hcf
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[11] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.RAW_FISH)
                    .durability(3)
                    .name(CC.YELLOW + "Combo")
                    .lore(listOf(
                        if (kit.kitData.combo) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.combo) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.combo = !kit.kitData.combo
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[15] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.YELLOW + "Ranked")
                    .lore(listOf(
                        if (kit.kitData.ranked) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.ranked) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.ranked = !kit.kitData.ranked
                kit.save()

                if (kit.kitData.ranked) {
                    val queue = Queue(kit, true)
                    PracticePlugin.instance.queueManager.queues.add(queue)
                }else {
                    PracticePlugin.instance.queueManager.queues.removeIf { it.kit.name.equals(kit.name, false) && it.ranked }
                }
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[21] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.LEASH)
                    .name(CC.YELLOW + "Sumo")
                    .lore(listOf(
                        if (kit.kitData.sumo) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.sumo) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.sumo = !kit.kitData.sumo
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }



        toReturn[23] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .name(CC.YELLOW + "Boxing")
                    .lore(listOf(
                        if (kit.kitData.boxing) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.boxing) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.boxing = !kit.kitData.boxing
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }
        // lemme figure out how kotlin would do it sec..
        // what are you trying to do?
        // for every option avaliable in KitData section it will create a button at the next best location
        // there all the fields in the class
        for (field in kit.kitData.javaClass.fields) {

        }

        return toReturn
    }
}