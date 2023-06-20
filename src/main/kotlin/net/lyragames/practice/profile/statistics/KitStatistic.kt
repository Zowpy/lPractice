package net.lyragames.practice.profile.statistics

import net.lyragames.llib.item.CustomItemStack
import net.lyragames.llib.utils.CC
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.match.Match
import net.lyragames.practice.match.MatchType
import net.lyragames.practice.match.impl.TeamMatch
import net.lyragames.practice.profile.Profile
import net.lyragames.practice.profile.ProfileState
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
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
        editedKits.set(index, loadout)
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

        val customItemStack = CustomItemStack(
            player.uniqueId, ItemBuilder(Material.BOOK).enchantment(Enchantment.DURABILITY).addFlags(ItemFlag.HIDE_ENCHANTS)
                .name(CC.RED + "Default").build()
        )
        customItemStack.isRightClick = true
        customItemStack.isRemoveOnClick = true
        customItemStack.clicked = Consumer { event ->
            val player1 = event.player

            if (profile!!.state == ProfileState.MATCH) {
                val match = Match.getByUUID(profile.match!!)

                if (match!!.getMatchType() == MatchType.TEAM || match.getMatchType() == MatchType.BEDFIGHTS) {
                    val team = (match as TeamMatch).getTeamByPlayer(player.uniqueId)

                    val content = kit!!.content.clone()

                    val toBeChanged = content.filter { it.type == Material.WOOL || it.type == Material.STAINED_CLAY }

                    if (team!!.name.equals("Red", true)) {
                        toBeChanged.forEach { it.durability = 14 }
                    } else {
                        toBeChanged.forEach { it.durability = 11 }
                    }

                    player1.inventory.contents = content
                }else {
                    player1.inventory.contents = kit!!.content
                }
            }else {
                player1.inventory.contents = kit!!.content
            }

            player1.inventory.armorContents = kit.armorContent
            player1.updateInventory()
        }

        customItemStack.create()

        player.inventory.setItem(8, customItemStack.itemStack)

        for (editedKit in profile!!.getKitStatistic(kit?.name!!)?.editedKits!!) {
            if (editedKit == null) continue

            val item = CustomItemStack(
                player.uniqueId, ItemBuilder(Material.BOOK).enchantment(Enchantment.DURABILITY).addFlags(ItemFlag.HIDE_ENCHANTS)
                    .name(CC.RED + editedKit.name).build()
            )

            item.isRightClick = true
            item.isRemoveOnClick = true
            item.clicked = Consumer { event ->
                val player1 = event.player

                player1.inventory.contents = editedKit.content
                player1.inventory.armorContents = editedKit.armorContent
                player1.updateInventory()
            }

            item.create()
            player.inventory.setItem(i, item.itemStack)

            if (i++ == 8) i++
        }
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