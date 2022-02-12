package net.lyragames.practice.arena

import lombok.Getter
import lombok.Setter
import org.bukkit.Location

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */
@Getter
@Setter
class Arena(private val name: String) {

    val l1: Location? = null
    val l2: Location? = null
    val min: Location? = null
    val max: Location? = null

    val isSetup: Boolean
        get() = l1 != null && l2 != null && min != null && max != null
}