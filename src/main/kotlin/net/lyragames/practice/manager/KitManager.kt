package net.lyragames.practice.manager

import net.lyragames.practice.PracticePlugin
import net.lyragames.practice.kit.Kit
import net.lyragames.practice.utils.InventoryUtil

/**
 * This Project is property of Zowpy & EliteAres © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
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

            kit.displayItem = InventoryUtil.deserializeItemStack(section.getString("icon"))


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
            kit.kitData.boxing = section.getBoolean("kitData.boxing")
            kit.kitData.enabled = section.getBoolean("kitData.enabled")
            kit.kitData.mlgRush = section.getBoolean("kitData.mlgRush")
            kit.kitData.ffa = section.getBoolean("kitData.ffa")
            kit.kitData.bedFights = section.getBoolean("kitData.bedFights")
            kit.kitData.fireballFight = section.getBoolean("kitData.fireballFight", false)
            kit.kitData.hunger = section.getBoolean("kitData.hunger")
            kit.kitData.regeneration = section.getBoolean("kitData.regeneration")
            kit.kitData.bridge = section.getBoolean("kitData.bridge")
            kit.kitData.fallDamage = section.getBoolean("kitData.fallDamage")

            Kit.kits.add(kit)
        }
    }
}