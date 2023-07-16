package net.lyragames.practice.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullBuilder {
    private final ItemBuilder stackBuilder;
    private String owner;

    SkullBuilder(ItemBuilder stackBuilder) {
        this.stackBuilder = stackBuilder;
    }

    public SkullBuilder setOwner(String ownerName) {
        this.owner = ownerName;
        return this;
    }

    public ItemStack buildSkull() {
        ItemStack skull = this.stackBuilder.type(Material.SKULL_ITEM).durability(3).build();
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setOwner(this.owner);
        skull.setItemMeta(meta);
        return skull;
    }
}
