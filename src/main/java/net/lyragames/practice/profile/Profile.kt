package net.lyragames.practice.profile

import net.lyragames.practice.kit.EditedKit
import net.lyragames.practice.kit.Kit
import java.util.*
import java.util.stream.Collectors

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

class Profile(val uuid: UUID, val name: String) {

    val editedKits: List<EditedKit> = ArrayList()
    var match: UUID? = null

    fun getEditKitsByKit(kit: Kit): List<EditedKit> {
        return editedKits.stream().filter { editedKit: EditedKit -> editedKit.originalKit == kit.name }
            .collect(Collectors.toList())
    }

    companion object {
        @JvmStatic
        private val profiles: List<Profile?> = LinkedList()

        @JvmStatic
        fun getByUUID(uuid: UUID): Profile? {
            return profiles.stream().filter { profile: Profile? -> profile?.uuid == uuid }
                .findFirst().orElse(null)
        }
    }
}