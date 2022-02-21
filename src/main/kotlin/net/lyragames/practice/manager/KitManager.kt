package net.lyragames.practice.manager

import net.lyragames.llib.utils.InventoryUtil
import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import org.bukkit.Material


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/16/2022
 * Project: lPractice
 */

object KitManager {

    fun load() {
        val configFile = PracticePlugin.instance.kitsFile

        if (configFile.getConfigurationSection("kits") == null) return

        for (key in configFile.getConfigurationSection("kits").getKeys(false)) {
            val section = configFile.getConfigurationSection("kits.$key")
            val kit = Kit(key)

            kit.displayItem = Material.valueOf(section.getString("material"))

            if (!section.getString("content").equals("null", false)) {
                kit.content = InventoryUtil.deserializeInventory(section.getString("content"))
            }

            if (!section.getString("armorContent").equals("null", false)) {
                kit.armorContent = InventoryUtil.deserializeInventory(section.getString("armorContent"))
            }

            kit.kitData.build = section.getBoolean("kitData.build")
            kit.kitData.combo = section.getBoolean("kitData.combo")
            kit.kitData.hcf = section.getBoolean("kitData.hcf")
            kit.kitData.ranked = section.getBoolean("kitData.ranked")
            kit.kitData.sumo = section.getBoolean("kitData.sumo")

            Kit.kits.add(kit)
        }
    }
}