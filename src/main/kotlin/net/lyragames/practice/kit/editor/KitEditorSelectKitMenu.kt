package net.lyragames.practice.kit.editor

import lombok.AllArgsConstructor
import net.lyragames.menu.Button
import net.lyragames.menu.ItemBuilder
import net.lyragames.menu.Menu
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
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
        return "&eSelect a kit"
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        Kit.kits.forEach { kit ->
            //if (kit.isEnabled()) {
                buttons[buttons.size] = KitDisplayButton(kit)
           //}
        }
        return buttons
    }

    private class KitDisplayButton(private val kit: Kit) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(kit.displayItem)
                .name("&e" + kit.name)
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