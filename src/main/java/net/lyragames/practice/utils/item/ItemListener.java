package net.lyragames.practice.utils.item;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        CustomItemStack customItemStack = CustomItemStack.getByInteraction(event);

        if (customItemStack == null) return;

        if (customItemStack.isRightClick() && !event.getAction().name().contains("RIGHT")) {
            return;
        }

        customItemStack.getClicked().accept(event);

        if (customItemStack.isRemoveOnClick()) {
            CustomItemStack.getCustomItemStacks().remove(customItemStack);
        }
    }
}
