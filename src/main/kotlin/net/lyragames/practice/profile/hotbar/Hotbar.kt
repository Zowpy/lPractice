package net.lyragames.practice.profile.hotbar

import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.practice.kit.editor.KitEditorSelectKitMenu
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
            player.inventory.setItem(0, createCustomItem(
                player,
                ItemBuilder(Material.IRON_SWORD).name("&eUnranked").addFlags(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build()
            ) { QueueMenu(false).openMenu(player) }.itemStack)

            player.inventory.setItem(1, createCustomItem(
                player,
                ItemBuilder(Material.DIAMOND_SWORD).name("&eRanked").addFlags(ItemFlag.HIDE_ATTRIBUTES).setUnbreakable(true).build()
            ) { QueueMenu(true).openMenu(player) }.itemStack)

            player.inventory.setItem(2, createCustomItem(
                player,
                ItemBuilder(Material.BOOK).name("&eEdit Kit").addFlags(ItemFlag.HIDE_ATTRIBUTES).build()
            ) { KitEditorSelectKitMenu().openMenu(player) }.itemStack)
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
}