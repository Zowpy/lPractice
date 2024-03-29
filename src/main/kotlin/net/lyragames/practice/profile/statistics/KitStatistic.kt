package net.lyragames.practice.profile.statistics

import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchType
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import net.lyragames.practice.utils.CC
import net.lyragames.practice.utils.InventoryUtil
import net.lyragames.practice.utils.ItemBuilder
import net.lyragames.practice.utils.item.CustomItemStack
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/15/2022
 * Project: lPractice
 */

class KitStatistic constructor(val kit: String) {

    var elo = 1000
    var peakELO = 1000
    var wins = 0
    var losses = 0

    var rankedLosses = 0
    var rankedWins = 0
    var currentStreak = 0
    var bestStreak = 0

    var editedKits: MutableList<EditedKit?> = mutableListOf(null, null, null, null)

    fun replaceKit(index: Int, loadout: EditedKit?) {
        editedKits[index] = loadout
    }

    fun deleteKit(loadout: EditedKit?) {
        for (i in 0..3) {
            if (editedKits[i] == loadout) {
                editedKits[i] = null
                break
            }
        }
    }

    fun generateBooks(player: Player) {
        val profile = Profile.getByUUID(player.uniqueId)
        var i = 0
        val kit = Kit.getByName(this.kit)

        if (editedKits.isEmpty() || editedKits.none { it != null }) {
            giveContents(player, profile!!, kit!!.content, kit.armorContent, false)
            return
        }

        if (profile!!.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)
            val matchPlayer = match!!.getMatchPlayer(profile.uuid)

            if (matchPlayer!!.selectedKitArmor != null || matchPlayer.selectedKitContent != null) {
                giveContents(player, profile, matchPlayer.selectedKitContent!!, matchPlayer.selectedKitArmor!!, false)
                return
            }
        }

        val customItemStack = CustomItemStack(
            player.uniqueId, ItemBuilder(Material.BOOK)
                .enchantment(Enchantment.DURABILITY)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
                .name(CC.RED + "Default").build()
        )

        customItemStack.rightClick = true
        customItemStack.removeOnClick = true
        customItemStack.clicked = Consumer {
            giveContents(player, profile, kit!!.content, kit.armorContent, true)
        }

        customItemStack.create()

        player.inventory.setItem(8, customItemStack.itemStack)

        for (editedKit in profile.getKitStatistic(kit?.name!!)?.editedKits!!) {
            if (editedKit == null) continue

            val item = CustomItemStack(
                player.uniqueId,
                ItemBuilder(Material.BOOK)
                    .enchantment(Enchantment.DURABILITY)
                    .addFlags(ItemFlag.HIDE_ENCHANTS)
                    .name("${CC.RED}${editedKit.name}")
                    .build()
            )

            item.rightClick = true
            item.removeOnClick = true
            item.clicked = Consumer {
                giveContents(player, profile, editedKit.content!!, editedKit.armorContent!!, true)
            }

            item.create()
            player.inventory.setItem(i, item.itemStack)

            if (i++ == 8) i++
        }
    }

    private fun giveContents(
        player: Player,
        profile: Profile,
        contents: Array<ItemStack>,
        armorContent: Array<ItemStack>,
        edit: Boolean
    ) {
        if (profile.state == ProfileState.MATCH) {
            val match = Match.getByUUID(profile.match!!)

            if (edit) {
                val matchPlayer = match!!.getMatchPlayer(player.uniqueId)

                matchPlayer!!.selectedKitContent = contents
                matchPlayer.selectedKitArmor = armorContent
            }

            if (match!!.getMatchType() == MatchType.TEAM || match.getMatchType() == MatchType.BEDFIGHTS) {
                val team = (match as TeamMatch).getTeamByPlayer(player.uniqueId)

                val content = contents.toList().map { it.clone() }.toTypedArray()

                val toBeChanged = content.filter { it.type == Material.WOOL || it.type == Material.STAINED_CLAY }

                if (team!!.name.equals("Red", true)) {
                    toBeChanged.forEach { it.durability = 14 }
                } else {
                    toBeChanged.forEach { it.durability = 11 }
                }

                player.inventory.contents = content

                val armorContents = armorContent.toList().map { it.clone() }.toTypedArray()

                val armorToChange = armorContents.filter { it.type.name.contains("LEATHER") }

                if (team.name.equals("Red", true)) {
                    armorToChange.forEach { InventoryUtil.changeColor(it, Color.RED) }
                } else {
                    armorToChange.forEach { InventoryUtil.changeColor(it, Color.BLUE) }
                }

                player.inventory.armorContents = armorContents
                return
            }
        }

        player.inventory.contents = contents
        player.inventory.armorContents = armorContent

        player.updateInventory()
    }

    fun getKitCount(): Int {
        var i = 0
        for (editKit in editedKits) {
            if (editKit != null) {
                i++
            }
        }
        return i
    }

}