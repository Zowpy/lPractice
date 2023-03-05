package net.lyragames.practice.match.player

import lombok.Getter
import org.bukkit.Location
import java.util.*

/**
 * This Project is property of Zowpy & EliteAres © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 12/19/2021
 * Project: Practice
 */
@Getter
class TeamMatchPlayer(uuid: UUID, name: String, spawn: Location, val teamUniqueId: UUID) :
    MatchPlayer(uuid, name, spawn)