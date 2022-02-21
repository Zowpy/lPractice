package net.lyragames.practice.kit.editor

import lombok.AllArgsConstructor
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.llib.utils.PlayerUtil
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.DisplayButton
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.profile.hotbar.Hotbar
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class KitEditorMenu(private val index: Int): Menu() {

    private val ITEM_POSITIONS = intArrayOf(
        20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52,
        53
    )
    private val BORDER_POSITIONS = intArrayOf(1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 28, 37, 46)
    private val BORDER_BUTTON: Button = Button.placeholder(Material.COAL_BLOCK, 0.toByte(), " ")

    override fun getTitle(player: Player): String {
        val profile = Profile.getByUUID(player.uniqueId)
        return "&6&lEditing: &a" + profile?.kitEditorData?.kit?.name
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        for (border in BORDER_POSITIONS) {
            buttons[border] = BORDER_BUTTON
        }
        buttons[0] = CurrentKitButton()
        buttons[2] = SaveButton()
        buttons[6] = LoadDefaultKitButton()
        buttons[7] = ClearInventoryButton()
        buttons[8] = CancelButton(index)

        val profile = Profile.getByUUID(player.uniqueId)
        val kit = profile?.kitEditorData?.kit
        val kitLoadout: EditedKit? = profile?.kitEditorData?.selectedKit

        buttons[18] = ArmorDisplayButton(kitLoadout?.armorContent?.get(3))
        buttons[27] = ArmorDisplayButton(kitLoadout?.armorContent?.get(2))
        buttons[36] = ArmorDisplayButton(kitLoadout?.armorContent?.get(1))
        buttons[45] = ArmorDisplayButton(kitLoadout?.armorContent?.get(0))
      //  val items: Array<ItemStack>? = kit?.content//kit.getEditRules().getEditorItems()
      //  if (!items?.isNotEmpty()!!) {
        //    for (i: Int in items.indices) {
          //      buttons[ITEM_POSITIONS[i]] = InfiniteItemButton(items[i])
          //  }
      //  }
        return buttons
    }

    override fun onOpen(player: Player) {
        if (!isClosedByMenu) {
            PlayerUtil.reset(player)
            val profile = Profile.getByUUID(player.uniqueId)
            profile?.kitEditorData?.active = true

            if (profile?.kitEditorData?.selectedKit != null && profile.kitEditorData?.selectedKit?.content != null) {
                player.inventory.contents = profile.kitEditorData?.selectedKit?.content
            }
            player.updateInventory()
        }
    }

    override fun onClose(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        profile?.kitEditorData?.active = false
        if (profile?.state != ProfileState.MATCH) {
            object : BukkitRunnable() {
                override fun run() {
                    Hotbar.giveHotbar(profile!!)
                }
            }.runTask(PracticePlugin.instance)
        }
    }

    private class ArmorDisplayButton(private val itemStack: ItemStack?) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return if (itemStack == null || itemStack.type === Material.AIR) {
                ItemStack(Material.AIR)
            } else ItemBuilder(itemStack.clone())
                .name(CC.AQUA + itemStack.type.name)
                .lore(CC.YELLOW + "This is automatically equipped.")
                .build()
        }
    }

    private class CurrentKitButton : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val profile = Profile.getByUUID(player.uniqueId)
            return ItemBuilder(Material.NAME_TAG)
                .name("&6&lEditing: &a${profile?.kitEditorData!!.kit!!.name}")
                .build()
        }
    }

    private class ClearInventoryButton : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .durability(7)
                .name("&e&lClear Inventory")
                .lore(
                    listOf(
                        "&eThis will clear your inventory",
                        "&eso you can start over."
                    )
                )
                .build()
        }

        override fun clicked(player: Player, i: Int, clickType: ClickType?, hb: Int) {
            playNeutral(player)
            player.inventory.contents = arrayOfNulls<ItemStack>(36)
            player.updateInventory()
        }

        override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
            return true
        }
    }

    @AllArgsConstructor
    private class LoadDefaultKitButton : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .durability(7)
                .name(CC.YELLOW + CC.BOLD + "Load default kit")
                .lore(
                    listOf(
                        CC.YELLOW + "Click this to load the default kit",
                        CC.YELLOW + "into the kit editing menu."
                    )
                )
                .build()
        }

        override fun clicked(player: Player, i: Int, clickType: ClickType?, hb: Int) {
            playNeutral(player)
            val profile = Profile.getByUUID(player.uniqueId)
            player.inventory.contents = profile?.kitEditorData!!.kit!!.content
            player.updateInventory()
        }

        override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean {
            return true
        }
    }

    private class SaveButton : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .durability(5)
                .name("&a&lSave")
                .lore(CC.translate("&eClick this to save your kit."))
                .build()
        }

        override fun clicked(player: Player, i: Int, clickType: ClickType?, hb: Int) {
            playNeutral(player)
            player.closeInventory()
            val profile = Profile.getByUUID(player.uniqueId)
            if (profile?.kitEditorData?.kit != null) {
                profile.kitEditorData!!.selectedKit?.content = player.inventory.contents
            }
            //Hotbar.giveHotbarItems(player)
            KitManagementMenu(profile?.kitEditorData?.kit!!).openMenu(player)
        }
    }

    private class CancelButton(private val index: Int) : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .durability(14)
                .name("&c&lCancel")
                .lore(
                    listOf(
                        "&eClick this to abort editing your kit,",
                        "&eand return to the kit menu."
                    )
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            playNeutral(player)
            val profile = Profile.getByUUID(player.uniqueId)
            if (profile?.kitEditorData?.kit != null) {
                val kitData = profile.getKitStatistic(profile.kitEditorData!!.kit!!.name)
                kitData?.replaceKit(index, null)
                KitManagementMenu(profile.kitEditorData?.kit!!).openMenu(player)
            }
        }


    }

    private class InfiniteItemButton(itemStack: ItemStack?) :
        DisplayButton(itemStack, false) {
        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbar: Int) {
            val inventory: Inventory = player.openInventory.topInventory
            val itemStack: ItemStack = inventory.getItem(slot)
            inventory.setItem(slot, itemStack)
            player.itemOnCursor = itemStack
            player.updateInventory()
        }
    }
}