package net.lyragames.practice.kit.editor

import lombok.AllArgsConstructor
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.BackButton
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.profile.Profile
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/20/2022
 * Project: lPractice
 */

class KitManagementMenu(val kit: Kit): Menu() {

    private val PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, 7.toByte(), " ")

    init {
        isPlaceholder = true
        isUpdateAfterClick = false
    }

    override fun getTitle(player: Player?): String {
        return "&eViewing " + kit.name + " kits"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        val profile = Profile.getByUUID(player.uniqueId)
        val kitLoadouts: MutableList<EditedKit?>? = profile?.getKitStatistic(kit.name)?.editedKits
        var startPos = -1
        for (i in 0..3) {
            startPos += 2
            val kitLoadout: EditedKit? = kitLoadouts?.get(i)

            buttons[startPos] = if (kitLoadout == null) CreateKitButton(i) else KitDisplayButton(kitLoadout)
            buttons[startPos + 18] = LoadKitButton(i)
            buttons[startPos + 27] = if (kitLoadout == null) PLACEHOLDER else RenameKitButton(kit, kitLoadout)
            buttons[startPos + 36] = if (kitLoadout == null) PLACEHOLDER else DeleteKitButton(kit, kitLoadout)
        }
        buttons[36] = BackButton(KitEditorSelectKitMenu())
        return buttons
    }

    override fun onClose(player: Player) {
        if (!isClosedByMenu) {
            val profile = Profile.getByUUID(player.uniqueId)
            profile?.kitEditorData?.kit = null
            profile?.save()
        }
    }

    private class DeleteKitButton(private val kit: Kit?, private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.STAINED_CLAY)
                .name("&cDelete")
                .durability(14)
                .lore(
                    listOf(
                        "&cClick to delete this kit.",
                        "&cYou will &lNOT &cbe able to",
                        "&crecover this KitLoadout."
                    )
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            val profile = Profile.getByUUID(player.uniqueId)
            profile?.getKitStatistic(kit?.name!!)?.deleteKit(kitLoadout)
            if (kit != null) {
                KitManagementMenu(kit).openMenu(player)
            }
        }
    }

    private class CreateKitButton(private val index: Int) : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.IRON_SWORD)
                .name("&a&lCreate Kit")
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            val profile = Profile.getByUUID(player.uniqueId)
            val kit = profile?.kitEditorData?.kit

            // TODO: this shouldn't be null but sometimes it is?
            if (kit == null) {
                player.closeInventory()
                return
            }
            val kitLoadout = EditedKit("Kit " + (index + 1))
            kitLoadout.armorContent = kit.armorContent
            kitLoadout.content = kit.content

            profile.getKitStatistic(kit.name)?.replaceKit(index, kitLoadout)
            profile.kitEditorData?.selectedKit = kitLoadout
            profile.save()
            KitEditorMenu(index).openMenu(player)
        }
    }

    private class RenameKitButton(private val kit: Kit, private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.SIGN)
                .name("&eRename")
                .lore(CC.translate("&eClick to rename this kit."))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarSlot: Int) {
            currentlyOpenedMenus[player.name]?.isClosedByMenu = true
            player.closeInventory()
           // player.sendMessage(Locale.KIT_EDITOR_START_RENAMING.format(kitLoadout.getCustomName()))
            val profile = Profile.getByUUID(player.uniqueId)
            profile?.kitEditorData?.kit = kit
            profile?.kitEditorData?.selectedKit = kitLoadout
            profile?.kitEditorData?.active = true
            profile?.kitEditorData?.rename = true
        }
    }

    private class LoadKitButton(private val index: Int) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.BOOK)
                .name("&aLoad/Edit")
                .lore(CC.translate("&eClick to edit this kit."))
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarSlot: Int) {
            val profile = Profile.getByUUID(player.uniqueId)

            // TODO: this shouldn't be null but sometimes it is?
            if (profile?.kitEditorData?.kit == null) {
                player.closeInventory()
                return
            }
            val kit: EditedKit = profile.kitEditorData?.kit?.name?.let { profile.getKitStatistic(it)?.editedKits?.get(index) } ?: EditedKit("Kit " + (index + 1))

            if (kit.content == null || kit.armorContent == null) {
                kit.content = profile.kitEditorData?.kit?.content
                kit.armorContent = profile.kitEditorData?.kit?.armorContent
            }

            kit.originalKit = profile.kitEditorData?.kit?.name

            profile.kitEditorData?.kit?.name?.let { profile.getKitStatistic(it)?.replaceKit(index, kit) }
            profile.kitEditorData?.selectedKit = kit
            KitEditorMenu(index).openMenu(player)
        }
    }

    @AllArgsConstructor
    private class KitDisplayButton(private val kitLoadout: EditedKit) : Button() {
        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.BOOK)
                .name("&a" + kitLoadout.name)
                .build()
        }
    }
}