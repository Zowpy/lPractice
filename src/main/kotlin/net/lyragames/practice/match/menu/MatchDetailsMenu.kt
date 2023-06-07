package net.lyragames.practice.match.menu

import net.lyragames.llib.utils.*
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.DisplayButton
import net.lyragames.practice.match.snapshot.MatchSnapshot
import net.lyragames.practice.utils.PotionGradeUtil
import org.apache.commons.lang.StringEscapeUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import java.util.*
import java.util.function.Consumer


/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 2/25/2022
 * Project: lPractice
 */

class MatchDetailsMenu(private val snapshot: MatchSnapshot) : Menu() {

    override fun getTitle(player: Player?): String {
        return "Inventory of ${snapshot.username}"
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        val fixedContents = InventoryUtil.fixInventoryOrder(snapshot.contents)

        for (i in fixedContents.indices) {
            val itemStack = fixedContents[i]

            if (itemStack != null && itemStack.type !== Material.AIR) {
                buttons[i] = DisplayButton(itemStack, true)
            }

        }
        for (i in 0 until snapshot.armor.size) {
            val itemStack = snapshot.armor[i]

            if (itemStack != null && itemStack.type !== Material.AIR) {
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

    private class SwitchInventoryButton(private val opponent: UUID) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            val snapshot = MatchSnapshot.getByUuid(opponent)

            return if (snapshot != null) {
                ItemBuilder(Material.LEVER)
                    .name("${CC.PRIMARY}Opponent's Inventory")
                    .lore("${CC.PRIMARY}Switch to ${CC.SECONDARY}${snapshot.username}${CC.PRIMARY}'s inventory")
                    .build()
            } else {
                ItemStack(Material.AIR)
            }
        }

        override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            val snapshot = MatchSnapshot.getByUuid(opponent)

            if (snapshot != null) {
                player?.chat("/matchsnapshot ${snapshot.uuid.toString()}")
            }
        }
    }

    private class HealthButton(private val health: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.MELON)
                .name("${CC.PRIMARY}Health: ${CC.SECONDARY}${health}/10 &4" + StringEscapeUtils.unescapeJava("\u2764"))
                .amount((if (health == 0) 1 else health))
                .build()
        }
    }

    private class HungerButton(private val hunger: Int) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.COOKED_BEEF)
                .name("${CC.PRIMARY}Hunger: ${CC.PRIMARY}$hunger/20")
                .amount((if (hunger == 0) 1 else hunger))
                .build()
        }
    }

    private class EffectsButton(private val effects: Collection<PotionEffect?>) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            val builder = ItemBuilder(Material.POTION).name("${CC.PRIMARY}Potion Effects")

            if (effects.isEmpty()) {
                builder.lore("${CC.PRIMARY}No potion effects")
            } else {
                val lore: MutableList<String> = ArrayList()

                effects.forEach(Consumer { effect ->
                    val name: String =
                        PotionUtil.getName(effect?.type).toString() + " " + (effect?.amplifier?.plus(1))
                    val duration =
                        " (" + (effect?.duration?.div(20))?.times(1000)
                            ?.let { TimeUtil.millisToTimer(it.toLong()).toString() } + ")"
                    lore.add("${CC.SECONDARY}$name${CC.PRIMARY}$duration")
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
                .name("${CC.PRIMARY}Potions")
                .lore("${CC.SECONDARY}$name${CC.PRIMARY} had ${CC.SECONDARY} $potions ${CC.PRIMARY}potion ${(if (potions == 1) "" else "s")} left.")
                .build()
        }
    }

    private class StatisticsButton(private val snapshot: MatchSnapshot) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.PAPER)
                .name("${CC.PRIMARY}Statistics")
                .lore(
                    listOf(
                        "${CC.PRIMARY}Total Hits: ${CC.SECONDARY}${snapshot.totalHits}",
                        "${CC.PRIMARY}Longest Combo: ${CC.SECONDARY}${snapshot.longestCombo}",
                        "${CC.PRIMARY}Potion Grade: ${PotionGradeUtil.getGrade(snapshot.getPotionAccuracy())}",
                        "${CC.PRIMARY}Potions Thrown: ${CC.SECONDARY}${snapshot.potionsThrown}",
                        "${CC.PRIMARY}Potions Missed: ${CC.SECONDARY}${snapshot.potionsMissed}",
                        "${CC.PRIMARY}Potion Accuracy: ${CC.SECONDARY}${snapshot.getPotionAccuracy()}%"
                    )
                )
                .build()
        }
    }
}