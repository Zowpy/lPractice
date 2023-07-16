package net.lyragames.practice.utils.item

import net.lyragames.practice.utils.item.CustomItemStack.Companion.getByInteraction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class ItemListener : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val customItemStack = getByInteraction(event) ?: return
        if (customItemStack.rightClick && !event.action.name.contains("RIGHT")) {
            return
        }
        customItemStack.clicked!!.accept(event)
        if (customItemStack.removeOnClick) {
            CustomItemStack.customItemStacks.remove(customItemStack)
        }
    }
}