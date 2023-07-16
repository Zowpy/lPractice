package net.lyragames.practice.utils.item;

import lombok.Data;
import lombok.Getter;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class CustomItemStack {

    @Getter
    private static List<CustomItemStack> customItemStacks = new LinkedList<>();

    private final UUID uuid;
    private ItemStack itemStack;

    private boolean rightClick;
    private boolean removeOnClick;

    private Consumer<PlayerInteractEvent> clicked;

    public CustomItemStack(UUID uuid, ItemStack itemStack) {
        this.uuid = uuid;
        this.itemStack = itemStack;
    }

    public void create() {
        customItemStacks.add(this);
    }

    public static CustomItemStack getByInteraction(PlayerInteractEvent event) {
        return customItemStacks.stream().filter(customItemStack -> customItemStack.getUuid().equals(event.getPlayer().getUniqueId())
                && customItemStack.getItemStack().isSimilar(event.getItem())).findFirst().orElse(null);
    }
}
