package net.lyragames.practice.kit

import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class EditedKit(val name: String) {
    val originalKit: String? = null
    lateinit var content: Array<ItemStack>
    lateinit var armorContent: Array<ItemStack>
}