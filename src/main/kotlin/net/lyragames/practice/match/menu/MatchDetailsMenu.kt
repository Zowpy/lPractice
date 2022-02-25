package net.lyragames.practice.match.menu

import net.lyragames.llib.utils.InventoryUtil
import net.lyragames.llib.utils.ItemBuilder
import net.lyragames.llib.utils.PotionUtil
import net.lyragames.llib.utils.TimeUtil
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.DisplayButton
import net.lyragames.practice.match.snapshot.MatchSnapshot
import org.apache.commons.lang.StringEscapeUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import java.util.*
import java.util.function.Consumer


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/25/2022
 * Project: lPractice
 */

class MatchDetailsMenu(private val snapshot: MatchSnapshot) : Menu() {

    override fun getTitle(player: Player?): String {
        return "&6Inventory of " + snapshot.username
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        val fixedContents: Array<ItemStack> = InventoryUtil.fixInventoryOrder(snapshot.contents)

        for (i in fixedContents.indices) {
            val itemStack: ItemStack = fixedContents[i]
            if (itemStack != null && itemStack.getType() !== Material.AIR) {
                buttons[i] = DisplayButton(itemStack, true)
            }
        }
        for (i in 0 until snapshot.armor.size) {
            val itemStack: ItemStack = snapshot.armor.get(i)
            if (itemStack != null && itemStack.getType() !== Material.AIR) {
                buttons[39 - i] = DisplayButton(itemStack, true)
            }
        }
        var pos = 45
        buttons[pos++] = HealthButton(snapshot.health.toInt())
        buttons[pos++] = HungerButton(snapshot.hunger)
        buttons[pos++] = EffectsButton(snapshot.effects!!)
        if (snapshot.shouldDisplayRemainingPotions()) {
            buttons[pos++] = PotionsButton(snapshot.username!!, snapshot.getRemainingPotions())
        }
        buttons[pos] = StatisticsButton(snapshot)
        if (this.snapshot.opponent != null) {
            buttons[53] = SwitchInventoryButton(this.snapshot.opponent!!)
        }
        return buttons
    }

    override fun onOpen(player: Player) {
        //player.sendMessage(Locale.VIEWING_INVENTORY.format(snapshot.getUsername()))
    }

    private class SwitchInventoryButton(private val opponent: UUID) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            val snapshot = MatchSnapshot.snapshots[opponent]
            return if (snapshot != null) {
                ItemBuilder(Material.LEVER)
                    .name("&6Opponent's Inventory")
                    .lore("&eSwitch to &a" + snapshot.username + "&e's inventory")
                    .build()
            } else {
                ItemStack(Material.AIR)
            }
        }

        override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            val snapshot = MatchSnapshot.snapshots[opponent]

            if (snapshot != null) {
                player?.chat("/matchsnapshot ${snapshot.uuid.toString()}")
            }
        }
    }

    private class HealthButton(private val health: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.MELON)
                .name("&6Health: &a" + health + "/10 &4" + StringEscapeUtils.unescapeJava("\u2764"))
                .amount((if (health == 0) 1 else health))
                .build()
        }
    }

    private class HungerButton(private val hunger: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.COOKED_BEEF)
                .name("&6Hunger: &a$hunger/20")
                .amount((if (hunger == 0) 1 else hunger))
                .build()
        }
    }

    private class EffectsButton(private val effects: Collection<PotionEffect?>) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            val builder: ItemBuilder = ItemBuilder(Material.POTION).name("&6&lPotion Effects")
            if (effects.isEmpty()) {
                builder.lore("&eNo potion effects")
            } else {
                val lore: MutableList<String> = ArrayList()
                effects.forEach(Consumer { effect ->
                    val name: String =
                        PotionUtil.getName(effect?.type).toString() + " " + (effect?.amplifier?.plus(1))
                    val duration =
                        " (" + (effect?.duration?.div(20))?.times(1000)
                            ?.let { TimeUtil.millisToTimer(it.toLong()).toString() } + ")"
                    lore.add("&a$name&e$duration")
                })
                builder.lore(lore)
            }
            return builder.build()
        }
    }

    private class PotionsButton(private val name: String, private val potions: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.POTION)
                .durability(16421)
                .amount(if (potions == 0) 1 else potions)
                .name("&6Potions")
                .lore("&a" + name + " &ehad &a" + potions + " &epotion" + (if (potions == 1) "" else "s") + " left.")
                .build()
        }
    }

    private class StatisticsButton(private val snapshot: MatchSnapshot) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.PAPER)
                .name("&6Statistics")
                .lore(
                    listOf(
                        "&aTotal Hits: &e" + snapshot.totalHits,
                        "&aLongest Combo: &e" + snapshot.longestCombo,
                        "&aPotions Thrown: &e" + snapshot.potionsThrown,
                        "&aPotions Missed: &e" + snapshot.potionsMissed,
                        "&aPotion Accuracy: &e" + snapshot.getPotionAccuracy()
                    )
                )
                .build()
        }
    }

}