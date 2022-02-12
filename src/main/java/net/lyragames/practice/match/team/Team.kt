package net.lyragames.practice.match.team;

import lombok.Data;
import net.lyragames.practice.match.player.TeamMatchPlayer;

import java.util.ArrayList;
import java.util.List;
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
public class Team {

    private final UUID uuid = UUID.randomUUID();
    private final List<TeamMatchPlayer> players = new ArrayList<>();
}
