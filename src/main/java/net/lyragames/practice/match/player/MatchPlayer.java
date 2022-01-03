package net.lyragames.practice.match.player;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Data
public class MatchPlayer {

    private UUID uuid;
    private String name;
    private boolean dead, offline;
    private Location spawn;

    public MatchPlayer(UUID uuid, String name, Location spawn) {
        this.uuid = uuid;
        this.name = name;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
