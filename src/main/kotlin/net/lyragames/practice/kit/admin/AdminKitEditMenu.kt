package net.lyragames.practice.kit.admin

import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.manager.FFAManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.ffa.FFA
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
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
        return "${CC.PRIMARY}Editing ${kit.name}"
    }

    override fun isUpdateAfterClick(): Boolean {
        return true
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        toReturn[0] = object: Button() {
            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.ENCHANTED_BOOK)
                    .name("${CC.PRIMARY}Enabled")
                    .lore(listOf(
                        if (kit.kitData.enabled) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.enabled) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.enabled = !kit.kitData.enabled
                kit.save()

                if (!kit.kitData.enabled) {
                    QueueManager.queues.filter { it.kit.name.equals(kit.name, false) }
                        .forEach { queue ->
                            queue.queuePlayers.stream().map { Profile.getByUUID(it.uuid) }
                                ?.forEach {
                                    it?.state = ProfileState.LOBBY
                                    it?.queuePlayer = null

                                    Hotbar.giveHotbar(it!!)

                                    it.player.sendMessage("${CC.RED}You have been removed from the queue.")
                                }
                            queue.kit.kitData.enabled = false
                        }
                }else {
                    if (QueueManager.queues.none { it.kit.name.equals(kit.name, false) }) {
                        val queue = Queue(kit, false)
                        QueueManager.queues.add(queue)

                        if (kit.kitData.ranked) {
                            val rankedQueue = Queue(kit, true)
                            QueueManager.queues.add(rankedQueue)
                        }
                    }else {
                        QueueManager.queues.filter { it.kit.name.equals(kit.name, false) }.forEach {
                            it.kit.kitData.enabled = true
                        }
                    }
                }
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[1] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.COBBLESTONE)
                    .name("${CC.PRIMARY}Build")
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


        toReturn[2] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.FENCE)
                    .name("${CC.PRIMARY}HCF")
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

        toReturn[3] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.RAW_FISH)
                    .durability(3)
                    .name("${CC.PRIMARY}Combo")
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

        toReturn[4] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.DIAMOND_SWORD)
                    .name("${CC.PRIMARY}Ranked")
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
                    QueueManager.queues.add(queue)
                }else {
                    QueueManager.queues.removeIf { it.kit.name.equals(kit.name, false) && it.ranked }
                }
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[5] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.LEASH)
                    .name("${CC.PRIMARY}Sumo")
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



        toReturn[6] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .name("${CC.PRIMARY}Boxing")
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

        toReturn[7] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.STICK)
                    .name("${CC.PRIMARY}MLGRush")
                    .lore(listOf(
                        if (kit.kitData.mlgRush) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.mlgRush) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.mlgRush = !kit.kitData.mlgRush
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[8] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.BED)
                    .name("${CC.PRIMARY}Bed Fights")
                    .lore(listOf(
                        if (kit.kitData.bedFights) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.bedFights) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.bedFights = !kit.kitData.bedFights
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[9] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.GOLD_SWORD)
                    .name("${CC.PRIMARY}FFA")
                    .lore(listOf(
                        if (kit.kitData.ffa) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.ffa) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.ffa = !kit.kitData.ffa
                kit.save()

                if (kit.kitData.ffa) {
                    if (FFAManager.getByKit(kit) == null) {
                        val ffa = FFA(kit)
                        FFAManager.ffaMatches.add(ffa)
                    }
                }
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[10] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.COOKED_BEEF)
                    .name("${CC.PRIMARY}Hunger")
                    .lore(listOf(
                        if (kit.kitData.hunger) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.hunger) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.hunger = !kit.kitData.hunger
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[11] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.BONE)
                    .name("${CC.PRIMARY}Regeneration")
                    .lore(listOf(
                        if (kit.kitData.regeneration) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.regeneration) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.regeneration = !kit.kitData.regeneration
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }
        toReturn[12] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.FEATHER)
                    .name("${CC.PRIMARY}Fall Damage")
                    .lore(listOf(
                        if (kit.kitData.fallDamage) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.fallDamage) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.fallDamage = !kit.kitData.fallDamage
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        toReturn[13] = object: Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.STAINED_CLAY)
                    .durability(11)
                    .name("${CC.PRIMARY}Bridge")
                    .lore(listOf(
                        if (kit.kitData.bridge) "${CC.GREEN}⚫ Enabled" else "${CC.RED}⚫ Enabled",
                        if (!kit.kitData.bridge) "${CC.GREEN}⚫ Disabled" else "${CC.RED}⚫ Disabled"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                kit.kitData.bridge = !kit.kitData.bridge
                kit.save()
            }

            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
                return true
            }
        }

        return toReturn
    }
}