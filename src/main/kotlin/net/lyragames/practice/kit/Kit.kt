package net.lyragames.practice.kit

import net.lyragames.llib.utils.InventoryUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.data.KitData
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class Kit(val name: String) {
    var displayItem: ItemStack = ItemStack(Material.IRON_SWORD)
    var content: Array<ItemStack> = arrayOf()
    var armorContent: Array<ItemStack> = arrayOf()
    val kitData = KitData()

    fun save() {
        val configFile = PracticePlugin.instance.kitsFile
        val section = configFile.createSection("kits.$name")

        section.set("icon", InventoryUtil.serializeItemStack(displayItem))
        if (content.isEmpty()) section.set("content", "null") else section.set("content", InventoryUtil.serializeInventory(content))
        if (armorContent.isEmpty()) section.set("armorContent", "null") else section.set("armorContent", InventoryUtil.serializeInventory(armorContent))

        val dataSection = section.createSection("kitData")
        dataSection.set("build", kitData.build)
        dataSection.set("combo", kitData.combo)
        dataSection.set("hcf", kitData.hcf)
        dataSection.set("ranked", kitData.ranked)
        dataSection.set("sumo", kitData.sumo)
        dataSection.set("boxing", kitData.boxing)
        dataSection.set("ffa", kitData.ffa)
        dataSection.set("enabled", kitData.enabled)
        dataSection.set("mlgRush", kitData.mlgRush)
        dataSection.set("bedFights", kitData.bedFights)
        dataSection.set("bridge", kitData.bridge)
        dataSection.set("hunger", kitData.hunger)
        dataSection.set("regeneration", kitData.regeneration)
        dataSection.set("fallDamage", kitData.fallDamage)

        configFile.save()
    }

    companion object {
        @JvmStatic
        val kits: MutableList<Kit> = mutableListOf()

        @JvmStatic
        fun getByName(name: String): Kit? {
            return kits.stream().filter { kit -> kit.name.equals(name, true) }
                .findFirst().orElse(null)
        }
    }
}