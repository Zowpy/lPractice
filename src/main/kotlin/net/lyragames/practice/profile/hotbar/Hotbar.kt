package net.lyragames.practice.profile.hotbar

import com.cryptomorin.xseries.XMaterial
import com.sun.org.apache.xpath.internal.operations.Bool
import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.practice.kit.editor.KitEditorSelectKitMenu
import net.lyragames.practice.manager.PartyManager
import net.lyragames.practice.manager.QueueManager
import net.lyragames.practice.match.ffa.menu.FFAChoosingMenu
import net.lyragames.practice.party.menu.PartyInformationMenu
import net.lyragames.practice.party.menu.ffa.PartyFFAKitSelect
import net.lyragames.practice.party.menu.split.PartySplitKitSelect
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.queue.menu.QueueMenu
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

object Hotbar {

    fun giveHotbar(profile: Profile) {
        val player = Bukkit.getPlayer(profile.uuid)
        player.inventory.clear()

        if (profile.state == ProfileState.LOBBY) {

            if (profile.party != null) {

                player.inventory.setItem(0, createCustomItem(
                    player,
                    ItemBuilder(Material.NETHER_STAR).name("&eParty Information").build()
                ) { PartyManager.getByUUID(profile.party!!)?.let { it1 -> PartyInformationMenu(it1).openMenu(player) } }.itemStack)

                player.inventory.setItem(4, createCustomItem(
                    player,
                    ItemBuilder(Material.GOLD_AXE).name("&eFFA").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
                ) { PartyManager.getByUUID(profile.party!!)?.let { it1 -> PartyFFAKitSelect(it1).openMenu(player) } }.itemStack)
                player.inventory.setItem(5, createCustomItem(
                    player,
                    ItemBuilder(Material.DIAMOND_SWORD).name("&eParty Split").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
                ) { PartyManager.getByUUID(profile.party!!)?.let { it1 -> PartySplitKitSelect(it1).openMenu(player) } }.itemStack)

                player.inventory.setItem(8, createCustomItem(
                    player,
                    ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&cLeave Party").build(), true
                ) { player.chat("/party leave") }.itemStack
                )

                return
            }

            player.inventory.setItem(0, createCustomItem(
                player,
                ItemBuilder(Material.IRON_SWORD).name("&eUnranked").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
            ) { QueueMenu(false).openMenu(player) }.itemStack)

            player.inventory.setItem(1, createCustomItem(
                player,
                ItemBuilder(Material.DIAMOND_SWORD).name("&eRanked").addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE).setUnbreakable(true).build()
            ) { QueueMenu(true).openMenu(player) }.itemStack)

            player.inventory.setItem(2, createCustomItem(
                player,
                ItemBuilder(Material.GOLD_SWORD).name("&eFFA").addFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build(),
            ) { FFAChoosingMenu().openMenu(player) }.itemStack)

            player.inventory.setItem(4, createCustomItem(
                player,
                ItemBuilder(Material.NETHER_STAR).name("&eCreate Party").build(), true
            ) { player.chat("/party create") }.itemStack)

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(Material.BOOK).name("&eEdit Kit").addFlags(ItemFlag.HIDE_ATTRIBUTES).build()
            ) { KitEditorSelectKitMenu().openMenu(player) }.itemStack)
        }else if (profile.state == ProfileState.QUEUE) {

            player.inventory.setItem(8, createCustomItem(
                player,
                ItemBuilder(XMaterial.RED_DYE.parseItem()).name("&eLeave Queue").build(), true
            ) {
                profile.state = ProfileState.LOBBY
                profile.queuePlayer = null
                QueueManager.getQueue(profile.uuid)?.queuePlayers?.removeIf { it.uuid == player.uniqueId }
                giveHotbar(profile)
            }.itemStack)

        }
    }

    private fun createCustomItem(player: Player, itemStack: ItemStack, consumer: Consumer<PlayerInteractEvent>): CustomItemStack {
        val customItemStack = CustomItemStack(player.uniqueId, itemStack)
        customItemStack.isRightClick = true
        customItemStack.clicked
        customItemStack.clicked = consumer

        if (!CustomItemStack.getCustomItemStacks().contains(customItemStack)) {
            customItemStack.create()
        }

        return customItemStack
    }

    private fun createCustomItem(player: Player, itemStack: ItemStack, remove: Boolean, consumer: Consumer<PlayerInteractEvent>): CustomItemStack {
        val customItemStack = CustomItemStack(player.uniqueId, itemStack)
        customItemStack.isRightClick = true
        customItemStack.clicked
        customItemStack.clicked = consumer
        customItemStack.isRemoveOnClick = remove

        if (!CustomItemStack.getCustomItemStacks().contains(customItemStack)) {
            customItemStack.create()
        }

        return customItemStack
    }
}