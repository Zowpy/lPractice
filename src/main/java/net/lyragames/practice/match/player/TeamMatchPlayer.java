package net.lyragames.practice.match.player;

import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Getter
public class TeamMatchPlayer extends MatchPlayer {

    private final UUID teamUniqueId;

    public TeamMatchPlayer(UUID uuid, String name, Location spawn, UUID teamUniqueId) {
        super(uuid, name, spawn);
        this.teamUniqueId = teamUniqueId;
    }
}
