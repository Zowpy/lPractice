package net.lyragames.practice.kit

import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy & EliteAres © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 12/19/2021
 * Project: Practice
 */

class EditedKit(var name: String) {
    var originalKit: String? = null
    var content: Array<ItemStack>? = null
    var armorContent: Array<ItemStack>? = null
}