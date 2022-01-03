package net.lyragames.practice.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Getter @Setter
public class Kit {

    private final String name;
    private ItemStack displayItem;
    private ItemStack[] content, armorContent;

    public Kit(String name) {
        this.name = name;
    }
}
