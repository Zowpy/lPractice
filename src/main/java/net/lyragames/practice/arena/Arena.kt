package net.lyragames.practice.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Getter @Setter
public class Arena {

    private final String name;
    private Location l1, l2, min, max;

    public Arena(String name) {
        this.name = name;
    }

    public boolean isSetup() {
        return l1 != null
                && l2 != null
                && min != null
                && max != null;
    }
}
