package net.lyragames.practice.kit

import lombok.Getter
import lombok.Setter
import net.lyragames.practice.kit.data.KitData
import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */
@Getter
@Setter
class Kit(val name: String) {
    val displayItem: ItemStack? = null
    lateinit var content: Array<ItemStack>
    lateinit var armorContent: Array<ItemStack>
    val kitData = KitData()
}