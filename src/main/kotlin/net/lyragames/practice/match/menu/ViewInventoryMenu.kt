package net.lyragames.practice.match.menu

import lombok.AllArgsConstructor
import net.lyragames.llib.utils.*
import net.lyragames.menu.Button
import net.lyragames.menu.Menu
import net.lyragames.menu.buttons.DisplayButton
import org.apache.commons.lang.StringEscapeUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import java.util.function.Consumer
import kotlin.math.roundToInt


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/25/2022
 * Project: lPractice
 */

class ViewInventoryMenu(private val target: Player) : Menu() {

    override fun getTitle(player: Player?): String {
        return CC.GOLD + target.name + "'s Inventory"
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttons: MutableMap<Int, Button> = mutableMapOf()
        if (player == null) {
            return buttons
        }
        val fixedContents: Array<ItemStack> = InventoryUtil.fixInventoryOrder(target.inventory.contents)
        for (i in fixedContents.indices) {
            val itemStack: ItemStack = fixedContents[i]
            if (itemStack.type !== Material.AIR) {
                buttons[i] = DisplayButton(itemStack, true)
            }
        }
        for (i in target.inventory.armorContents.indices) {
            val itemStack: ItemStack? = target.inventory.armorContents[i]
            if (itemStack != null && itemStack.type !== Material.AIR) {
                buttons[39 - i] = DisplayButton(itemStack, true)
            }
        }
        var pos = 45
        buttons[pos++] = HealthButton(if (target.health == 0.0) 0 else (target.health / 2.0).roundToInt())
        buttons[pos++] = HungerButton(target.foodLevel)
        buttons[pos] = EffectsButton(target.activePotionEffects)
        return buttons
    }

    override fun isAutoUpdate(): Boolean {
        return true
    }

    @AllArgsConstructor
    private class HealthButton(private val health: Int) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.MELON)
                .name("${CC.PRIMARY}Health: ${CC.SECONDARY}" + health + "/10 " + StringEscapeUtils.unescapeJava("\u2764"))
                .amount(if (health == 0) 1 else health)
                .build()
        }
    }

    @AllArgsConstructor
    private class HungerButton(private val hunger: Int) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(Material.COOKED_BEEF)
                .name("${CC.PRIMARY}Hunger: ${CC.SECONDARY}$hunger/20")
                .amount(if (hunger == 0) 1 else hunger)
                .build()
        }
    }

    @AllArgsConstructor
    private class EffectsButton(private val effects: Collection<PotionEffect>) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            val builder: ItemBuilder = ItemBuilder(Material.POTION).name("${CC.PRIMARY}Potion Effects")
            if (effects.isEmpty()) {
                builder.lore(CC.PRIMARY + "No effects")
            } else {
                val lore: MutableList<String> = ArrayList()
                effects.forEach(Consumer { effect: PotionEffect ->
                    val name = PotionUtil.getName(effect.type) + " " + (effect.amplifier + 1)
                    val duration =
                        " (" + TimeUtil.millisToTimer((effect.duration / 20 * 1000).toLong()).toString() + ")"
                    lore.add(CC.PRIMARY + name + CC.SECONDARY + duration)
                })
                builder.lore(lore)
            }
            return builder.build()
        }
    }
}