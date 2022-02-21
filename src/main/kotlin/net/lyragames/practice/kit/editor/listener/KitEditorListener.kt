package net.lyragames.practice.kit.editor.listener

import net.lyragames.llib.utils.CC
import net.lyragames.practice.kit.editor.KitManagementMenu
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.inventory.CraftingInventory


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/21/2022
 * Project: lPractice
 */

object KitEditorListener: Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onAsyncPlayerChatEvent(event: AsyncPlayerChatEvent) {
        val profile = Profile.getByUUID(event.player.uniqueId)
        if (profile?.kitEditorData?.isRenaming()!!) {
            event.isCancelled = true
            if (event.message.length > 16) {
                event.player.sendMessage(CC.RED + "The kit name must be under 16 characters!")
                return
            }
            val previousName = profile.kitEditorData?.selectedKit?.name
            val newName = CC.translate(event.message)
            event.player.sendMessage(CC.GREEN + "Successfully changed kit loadout name from ${CC.GOLD}${previousName}${CC.GREEN} to ${CC.GOLD}${newName}")
            val selectedKit = profile.kitEditorData?.kit
            profile.kitEditorData?.kit = null
            profile.kitEditorData?.selectedKit?.name = newName
            profile.kitEditorData?.active = false
            profile.kitEditorData?.rename = false
            if (profile.state != ProfileState.MATCH) {
                if (selectedKit != null) {
                    KitManagementMenu(selectedKit).openMenu(event.player)
                }
            }
        }
    }

   /* @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.item != null && (event.action == Action.RIGHT_CLICK_AIR ||
                    event.action == Action.RIGHT_CLICK_BLOCK)
        ) {
            val hotbarItem: HotbarItem = Hotbar.fromItemStack(event.item)
            if (hotbarItem != null) {
                var cancelled = true
                if (hotbarItem === HotbarItem.KIT_EDITOR) {
                    val profile: Profile = Profile.getByUuid(event.player.uniqueId)
                    if (profile.getState() === ProfileState.LOBBY || profile.getState() === ProfileState.QUEUEING) {
                        KitEditorSelectKitMenu().openMenu(event.player)
                    }
                } else {
                    cancelled = false
                }
                event.isCancelled = cancelled
            }
        }
    } */

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        if (event.whoClicked is Player) {
            val player: Player = event.whoClicked as Player
            if (event.clickedInventory != null && event.clickedInventory is CraftingInventory) {
                if (player.gameMode != GameMode.CREATIVE) {
                    event.isCancelled = true
                    return
                }
            }
            val profile = Profile.getByUUID(player.uniqueId)
            if (profile?.state != ProfileState.MATCH && player.gameMode == GameMode.SURVIVAL) {
                val clicked = event.clickedInventory
                if (profile?.kitEditorData?.active!!) {
                    if (clicked == player.openInventory.topInventory) {
                        if (event.cursor.type != Material.AIR &&
                            event.currentItem.type == Material.AIR ||
                            event.cursor.type != Material.AIR &&
                            event.currentItem.type != Material.AIR
                        ) {
                            event.isCancelled = true
                            event.cursor = null
                            player.updateInventory()
                        }
                    }
                } else {
                    if (clicked != null && clicked == player.inventory) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }
}