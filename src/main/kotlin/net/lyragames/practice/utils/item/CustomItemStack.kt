package net.lyragames.practice.utils.item

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Consumer

class CustomItemStack(val uuid: UUID, val itemStack: ItemStack) {
    var rightClick = false
    var removeOnClick = false
    var clicked: Consumer<PlayerInteractEvent>? = null

    fun create() {
        customItemStacks.add(this)
    }

    companion object {
        val customItemStacks: MutableList<CustomItemStack> = mutableListOf()

        @JvmStatic
        fun getByInteraction(event: PlayerInteractEvent): CustomItemStack? {
            return customItemStacks.firstOrNull {
                it.uuid == event.player.uniqueId && it.itemStack
                    .isSimilar(event.item)
            }
        }
    }
}