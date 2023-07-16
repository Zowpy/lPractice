package net.lyragames.practice.kit.editor

import me.zowpy.menu.Menu
import me.zowpy.menu.buttons.Button
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.ItemBuilder
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

class KitEditorSelectKitMenu: Menu() {

    override fun getTitle(player: Player?): String {
        return "Select a kit"
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        Kit.kits.forEach { kit ->
            if (kit.kitData.enabled) {
                buttons[buttons.size] = KitDisplayButton(kit)
           }
        }
        return buttons
    }

    private class KitDisplayButton(private val kit: Kit) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(kit.displayItem)
                .name("${CC.PRIMARY}${kit.name}")
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            player.closeInventory()
            val profile = Profile.getByUUID(player.uniqueId)
            profile?.kitEditorData?.kit = kit
            KitManagementMenu(kit).openMenu(player)
        }
    }
}